import type { FC } from "react";
import React from "react";
import { EuiFlexGroup, EuiImage, EuiLoadingLogo } from "@elastic/eui";

const BiblivreLoadingIcon: FC = () => (
  <EuiFlexGroup justifyContent="center" alignItems="center">
    <EuiLoadingLogo
      size="xl"
      logo={() => (
        <EuiImage
          size="original"
          src="/static/images/logo_biblivre_small_original.png"
          alt="Bredo logo"
        />
      )}
    />
  </EuiFlexGroup>
);

export default BiblivreLoadingIcon;
