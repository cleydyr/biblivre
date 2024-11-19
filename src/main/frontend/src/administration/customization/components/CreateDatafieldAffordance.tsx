import type { FormFieldEditorState } from "./types";
import type { FC } from "react";
import React, { Fragment, useState } from "react";
import EditDatafieldFlyout from "../EditDatafieldFlyout";
import { EuiButton } from "@elastic/eui";
import { FormattedMessage } from "react-intl";

function getInitialEditorState(): FormFieldEditorState {
  return {
    tag: "",
    name: "",
    indicatorsState: [
      {
        defined: false,
        description: "",
        translations: {},
      },
      {
        defined: false,
        description: "",
        translations: {},
      },
    ],
    materialTypes: [],
    subfields: [],
    repeatable: false,
    collapsed: false,
  };
}

type CreateFieldAffordanceProps = {
  onCreateFormData: (formFieldEditorState: FormFieldEditorState) => void;
};

const CreateDatafieldAffordance: FC<CreateFieldAffordanceProps> = ({
  onCreateFormData,
}) => {
  const [creatingDatafield, setCreatingDatafield] = useState<boolean>(false);

  return (
    <Fragment>
      {creatingDatafield && (
        <EditDatafieldFlyout
          mode="create"
          editorState={getInitialEditorState()}
          onClose={() => setCreatingDatafield(false)}
          onSave={(formFieldEditorState) => {
            onCreateFormData(formFieldEditorState);

            setCreatingDatafield(false);
          }}
        />
      )}
      <EuiButton
        fill
        onClick={() => setCreatingDatafield(true)}
        iconType="plusInCircle"
      >
        <FormattedMessage
          id="administration.customization.form.create"
          defaultMessage="Criar campo"
        />
      </EuiButton>
    </Fragment>
  );
};

export default CreateDatafieldAffordance;
