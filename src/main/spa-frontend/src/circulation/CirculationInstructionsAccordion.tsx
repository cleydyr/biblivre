import { EuiAccordion, EuiText } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

const CirculationInstructionsAccordion = () => {
  return (
    <EuiAccordion id='circulation-instructions'>
      <EuiText>
        <p>
          <FormattedMessage
            defaultMessage='O "Cadastro de Usuários" permitirá guardar informações sobre os leitores e funcionários da biblioteca para que seja possível realizar empréstimos, reservas e controlar o acesso destes usuários à biblioteca.

Antes de cadastrar um usuário é recomendado verificar se ele já está cadastrado, através da pesquisa simplificada, que buscará cada um dos termos digitados no campo selecionado ou através da pesquisa avançada, que confere um maior controle sobre os usuários localizados, permitindo, por exemplo, buscar usuários com multas pendentes.'
            id='circulation.instructions.1'
          />
        </p>
      </EuiText>
    </EuiAccordion>
  )
}

export default CirculationInstructionsAccordion
