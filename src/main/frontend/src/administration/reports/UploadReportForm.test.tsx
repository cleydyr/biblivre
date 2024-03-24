import { render, screen } from "@elastic/eui/lib/test/rtl";
import UploadReportForm from "./UploadReportForm";
import userEvent from "@testing-library/user-event";
import React from "react";

describe("UploadReportForm", () => {
  test("renders form with file picker", async () => {
    render(<UploadReportForm onSubmit={() => {}} />);

    // Assert that the form title is rendered
    expect(screen.getByText("Upload de relatório")).toBeInTheDocument();

    // Assert that the file picker is rendered
    expect(
      screen.getByLabelText("Selecionador de arquivo para upload de relatório")
    ).toBeInTheDocument();
  });

  test('calls onSubmit with selected file when "Enviar" button is clicked', async () => {
    const onSubmitMock = jest.fn();

    const user = userEvent.setup();

    render(<UploadReportForm onSubmit={onSubmitMock} />);

    // Select a file using the file picker
    const file = new File(["dummy content"], "dummy-file.jrxml", {
      type: "text/xml",
    });

    await user.upload(
      screen.getByLabelText("Selecionador de arquivo para upload de relatório"),
      file
    );

    // Click the "Enviar" button
    await user.click(screen.getByText("Enviar"));

    // Assert that onSubmit is called with the selected file
    expect(onSubmitMock).toHaveBeenCalledWith(file);
  });

  test("the file list is cleaned after submitting", async () => {
    const onSubmitMock = jest.fn();

    const user = userEvent.setup();

    render(<UploadReportForm onSubmit={onSubmitMock} />);

    expect(
      screen.queryByLabelText("Clear selected files")
    ).not.toBeInTheDocument();

    // Select a file using the file picker
    const file = new File(["dummy content"], "dummy-file.jrxml", {
      type: "text/xml",
    });

    await user.upload(
      screen.getByLabelText("Selecionador de arquivo para upload de relatório"),
      file
    );

    expect(
      (
        screen.getByLabelText(
          "Selecionador de arquivo para upload de relatório"
        ) as HTMLInputElement
      ).files?.length
    ).toEqual(1);

    expect(screen.getByLabelText("Clear selected files")).toBeInTheDocument();

    // Click the "Enviar" button
    await user.click(screen.getByText("Enviar"));

    expect(
      screen.queryByLabelText("Clear selected files")
    ).not.toBeInTheDocument();

    expect(onSubmitMock).toHaveBeenCalledWith(file);

    // Assert that the file picker is empty
    expect(
      (
        screen.getByLabelText(
          "Selecionador de arquivo para upload de relatório"
        ) as HTMLInputElement
      ).files?.length
    ).toEqual(0);
  });
});
