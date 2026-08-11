import { EuiAccordion, EuiText } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

const IntelligentSearchInstructionsAccordion = () => {
  return (
    <EuiAccordion
      buttonContent={
        <FormattedMessage
          defaultMessage='Como funciona a Pesquisa Inteligente'
          id='search.bibliographic.intelligent.instructions.title'
        />
      }
      id='intelligent-search-instructions'
    >
      <EuiText>
        <p>
          <FormattedMessage
            defaultMessage='A Pesquisa Inteligente combina busca por palavras-chave com busca semântica. Além de localizar termos nos registros, ela também encontra obras com significado semelhante à sua consulta — útil quando você descreve o que procura com suas próprias palavras, mesmo sem saber o título ou o autor exatos.'
            id='search.bibliographic.intelligent.instructions.1'
          />
        </p>
        <p>
          <FormattedMessage
            defaultMessage='Digite uma frase ou termos relacionados ao assunto desejado. Os resultados mesclam coincidências textuais e semânticas, priorizando os registros mais relevantes.'
            id='search.bibliographic.intelligent.instructions.2'
          />
        </p>
        <p>
          <FormattedMessage
            defaultMessage='A pesquisa bibliográfica clássica continua disponível no menu Pesquisa Bibliográfica para buscas por campos específicos, curingas e filtros avançados.'
            id='search.bibliographic.intelligent.instructions.3'
          />
        </p>
      </EuiText>
    </EuiAccordion>
  )
}

export default IntelligentSearchInstructionsAccordion
