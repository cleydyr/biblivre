import { expect, test, type Page, type Response } from "@playwright/test";

import { login, navigateToCirculationLending } from "./helpers/auth";

type LegacyResponse = {
  success: boolean;
  message?: string;
  data?: {
    id: number;
    accession_number?: string;
    lending?: { id: number };
  };
};

function isLendingAction(action: string) {
  return (response: Response) => {
    const postData = response.request().postData() ?? "";

    return (
      response.request().method() === "POST" &&
      postData.includes("module=circulation.lending") &&
      postData.includes(`action=${action}`)
    );
  };
}

async function legacyAction(
  page: Page,
  params: Record<string, string>,
): Promise<LegacyResponse> {
  return page.evaluate(async (payload) => {
    const schema = localStorage.getItem("biblivre.selectedSchema");
    const contextPath = window.location.pathname.split("/spa/")[0];
    const response = await fetch(`${contextPath}/`, {
      method: "POST",
      headers: {
        "content-type": "application/x-www-form-urlencoded;charset=UTF-8",
        ...(schema ? { "X-Biblivre-Schema": schema } : {}),
      },
      body: new URLSearchParams({
        controller: "json",
        ...payload,
      }).toString(),
    });

    return response.json() as Promise<LegacyResponse>;
  }, params);
}

async function createFixtures(page: Page, timestamp: number) {
  const user = await legacyAction(page, {
    module: "circulation.user",
    action: "save",
    id: "0",
    name: `E2E Lending ${timestamp}`,
    type: "1",
    status: "active",
    email: `e2e-lending-${timestamp}@example.com`,
  });
  expect(user.success, user.message).toBe(true);
  expect(user.data?.id).toBeDefined();

  const biblio = await legacyAction(page, {
    module: "cataloging.bibliographic",
    action: "save",
    oldId: "0",
    id: "0",
    from: "form",
    data: JSON.stringify({
      "245": [{ ind1: " ", ind2: " ", a: [`E2E Lending ${timestamp}`] }],
    }),
    material_type: "book",
    database: "main",
  });
  expect(biblio.success, biblio.message).toBe(true);
  expect(biblio.data?.id).toBeDefined();

  const holding = await legacyAction(page, {
    module: "cataloging.holding",
    action: "save",
    oldId: "0",
    id: "0",
    record_id: String(biblio.data!.id),
    availability: "available",
    from: "holding_form",
    data: "{}",
  });
  expect(holding.success, holding.message).toBe(true);
  expect(holding.data?.id).toBeDefined();
  expect(holding.data?.accession_number).toBeTruthy();

  return {
    userId: user.data!.id,
    userName: `E2E Lending ${timestamp}`,
    recordId: biblio.data!.id,
    holdingId: holding.data!.id,
    accessionNumber: holding.data!.accession_number!,
  };
}

test.describe("Circulation lending", () => {
  test("selects a patron, lends, renews, and returns a holding", async ({
    page,
  }) => {
    const timestamp = Date.now();
    let fixtures: Awaited<ReturnType<typeof createFixtures>> | undefined;
    let currentLendingId: number | undefined;

    await login(page);

    try {
      fixtures = await createFixtures(page, timestamp);
      await navigateToCirculationLending(page);

      await page
        .getByPlaceholder("Pesquise e selecione quem receberá o exemplar")
        .fill(fixtures.userName);
      const userSearchResponse = page.waitForResponse(
        isLendingAction("user_search"),
      );
      await page
        .getByPlaceholder("Pesquise e selecione quem receberá o exemplar")
        .press("Enter");
      await userSearchResponse;

      await page.getByRole("button", { name: "Selecionar usuário" }).click();
      await expect(page.getByText("Usuário selecionado")).toBeVisible();

      const holdingSearch = page.getByPlaceholder(
        "Escaneie o tombo ou pesquise o exemplar",
      );
      await holdingSearch.fill(fixtures.accessionNumber);
      const holdingSearchResponse = page.waitForResponse(
        isLendingAction("search"),
      );
      await holdingSearch.press("Enter");
      await holdingSearchResponse;

      const lendResponsePromise = page.waitForResponse(isLendingAction("lend"));
      await page.getByRole("button", { name: "Emprestar" }).click();
      const lendResponse = await lendResponsePromise;
      const lendBody = (await lendResponse.json()) as LegacyResponse;
      expect(lendBody.success, lendBody.message).toBe(true);
      currentLendingId = lendBody.data?.lending?.id;
      expect(currentLendingId).toBeDefined();

      await expect(
        page.getByRole("button", { name: "Imprimir recibo (1)" }),
      ).toBeEnabled();

      const renewResponsePromise = page.waitForResponse(
        isLendingAction("renew_lending"),
      );
      await page.getByRole("button", { name: "Renovar" }).first().click();
      const renewResponse = await renewResponsePromise;
      const renewBody = (await renewResponse.json()) as LegacyResponse;
      expect(renewBody.success, renewBody.message).toBe(true);
      currentLendingId = renewBody.data?.lending?.id;
      expect(currentLendingId).toBeDefined();

      await page
        .getByRole("button", { name: "Devolver ou tratar multa" })
        .click();
      await expect(page).toHaveURL(/\/spa\/circulation_return/);

      const returnSearch = page.getByPlaceholder(
        "Escaneie o tombo ou busque por título/autor",
      );
      await returnSearch.fill(fixtures.accessionNumber);
      const returnResponsePromise = page.waitForResponse(
        isLendingAction("return_immediate"),
      );
      await returnSearch.press("Enter");
      const returnResponse = await returnResponsePromise;
      const returnBody = (await returnResponse.json()) as LegacyResponse;
      expect(returnBody.success, returnBody.message).toBe(true);
      currentLendingId = undefined;

      await expect(page.getByText("Devolução realizada")).toBeVisible();
    } finally {
      if (currentLendingId !== undefined) {
        await legacyAction(page, {
          module: "circulation.lending",
          action: "return_immediate",
          id: String(currentLendingId),
        });
      }

      if (fixtures) {
        await legacyAction(page, {
          module: "cataloging.holding",
          action: "delete",
          id: String(fixtures.holdingId),
        });
        await legacyAction(page, {
          module: "cataloging.bibliographic",
          action: "delete",
          id: String(fixtures.recordId),
        });
        await legacyAction(page, {
          module: "circulation.user",
          action: "delete",
          id: String(fixtures.userId),
        });
        await legacyAction(page, {
          module: "circulation.user",
          action: "delete",
          id: String(fixtures.userId),
        });
      }
    }
  });
});
