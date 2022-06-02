import {
  EuiBadge,
  EuiBasicTable,
  EuiButton,
  EuiButtonEmpty,
  EuiFieldSearch,
  EuiFlexGroup,
  EuiFlexItem,
  EuiForm,
  EuiIcon,
  EuiLink,
  EuiSelect,
  EuiSpacer
} from '@elastic/eui';

import '@elastic/eui/dist/eui_theme_light.css';

interface Sort {
  unsorted: boolean
  sorted: boolean
  empty: boolean
}

interface Pageable {
  paged: boolean
  pageNumber: number
  offset: number
  pageSize: number
  unpaged: boolean
  sort: Sort
}

type AccessCardStatus = 'AVAILABLE' | 'IN_USE' | 'BLOCKED' | 'IN_USE_AND_BLOCKED' | 'CANCELLED'

interface Card {
  code: string,
  createdBy: number,
  created: string,
  modified: string,
  modifiedBy: number,
  id: number,
  accessCardStatus: AccessCardStatus
}

interface SearchData {
  number: number
  last: boolean
  numberOfElements: number
  size: number
  totalPages: number
  pageable: Pageable
  sort: Sort
  content: Card[]
  first: boolean
  totalElements: number,
  empty: boolean
}
interface Response {
  search: SearchData,
  success: boolean,
}

const response: Response = {
  "search": {
    "number": 0,
    "last": true,
    "numberOfElements": 7,
    "size": 25,
    "totalPages": 1,
    "pageable": {
      "paged": true,
      "pageNumber": 0,
      "offset": 0,
      "pageSize": 25,
      "unpaged": false,
      "sort": {
        "unsorted": true,
        "sorted": false,
        "empty": true
      }
    },
    "sort": {
      "unsorted": true,
      "sorted": false,
      "empty": true
    },
    "content": [
      {
        "code": "asebbasase",
        "createdBy": 1,
        "created": "2022-05-31T08:19:51.662",
        "modified": "2022-05-31T08:19:51.662",
        "modifiedBy": 1,
        "id": 13,
        "accessCardStatus": "IN_USE"
      },
      {
        "code": "fawefqwef",
        "createdBy": 1,
        "created": "2022-05-30T21:35:09.536",
        "modified": "2022-05-30T21:35:09.536",
        "modifiedBy": 1,
        "id": 5,
        "accessCardStatus": "CANCELLED"
      },
      {
        "code": "teste",
        "createdBy": 1,
        "created": "2022-05-30T20:54:24.322",
        "modified": "2022-05-30T20:54:24.322",
        "modifiedBy": 1,
        "id": 6,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "asetasetas",
        "createdBy": 1,
        "created": "2022-05-30T21:25:25.162",
        "modified": "2022-05-30T21:25:25.162",
        "modifiedBy": 1,
        "id": 4,
        "accessCardStatus": "BLOCKED"
      },
      {
        "code": "fasefaseqw",
        "createdBy": 1,
        "created": "2022-05-30T21:19:41.543715",
        "modified": "2022-05-30T21:19:41.543715",
        "modifiedBy": 1,
        "id": 3,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "testes",
        "createdBy": 1,
        "created": "2022-05-30T21:14:32.549051",
        "modified": "2022-05-30T21:14:32.549051",
        "modifiedBy": 1,
        "id": 1,
        "accessCardStatus": "IN_USE_AND_BLOCKED"
      },
      {
        "code": "asetase",
        "createdBy": 1,
        "created": "2022-05-30T21:17:53.578356",
        "modified": "2022-05-30T21:17:53.578356",
        "modifiedBy": 1,
        "id": 2,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "asebbasase",
        "createdBy": 1,
        "created": "2022-05-31T08:19:51.662",
        "modified": "2022-05-31T08:19:51.662",
        "modifiedBy": 1,
        "id": 13,
        "accessCardStatus": "IN_USE"
      },
      {
        "code": "fawefqwef",
        "createdBy": 1,
        "created": "2022-05-30T21:35:09.536",
        "modified": "2022-05-30T21:35:09.536",
        "modifiedBy": 1,
        "id": 5,
        "accessCardStatus": "CANCELLED"
      },
      {
        "code": "teste",
        "createdBy": 1,
        "created": "2022-05-30T20:54:24.322",
        "modified": "2022-05-30T20:54:24.322",
        "modifiedBy": 1,
        "id": 6,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "asetasetas",
        "createdBy": 1,
        "created": "2022-05-30T21:25:25.162",
        "modified": "2022-05-30T21:25:25.162",
        "modifiedBy": 1,
        "id": 4,
        "accessCardStatus": "BLOCKED"
      },
      {
        "code": "fasefaseqw",
        "createdBy": 1,
        "created": "2022-05-30T21:19:41.543715",
        "modified": "2022-05-30T21:19:41.543715",
        "modifiedBy": 1,
        "id": 3,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "testes",
        "createdBy": 1,
        "created": "2022-05-30T21:14:32.549051",
        "modified": "2022-05-30T21:14:32.549051",
        "modifiedBy": 1,
        "id": 1,
        "accessCardStatus": "IN_USE_AND_BLOCKED"
      },
      {
        "code": "asetase",
        "createdBy": 1,
        "created": "2022-05-30T21:17:53.578356",
        "modified": "2022-05-30T21:17:53.578356",
        "modifiedBy": 1,
        "id": 2,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "asebbasase",
        "createdBy": 1,
        "created": "2022-05-31T08:19:51.662",
        "modified": "2022-05-31T08:19:51.662",
        "modifiedBy": 1,
        "id": 13,
        "accessCardStatus": "IN_USE"
      },
      {
        "code": "fawefqwef",
        "createdBy": 1,
        "created": "2022-05-30T21:35:09.536",
        "modified": "2022-05-30T21:35:09.536",
        "modifiedBy": 1,
        "id": 5,
        "accessCardStatus": "CANCELLED"
      },
      {
        "code": "teste",
        "createdBy": 1,
        "created": "2022-05-30T20:54:24.322",
        "modified": "2022-05-30T20:54:24.322",
        "modifiedBy": 1,
        "id": 6,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "asetasetas",
        "createdBy": 1,
        "created": "2022-05-30T21:25:25.162",
        "modified": "2022-05-30T21:25:25.162",
        "modifiedBy": 1,
        "id": 4,
        "accessCardStatus": "BLOCKED"
      },
      {
        "code": "fasefaseqw",
        "createdBy": 1,
        "created": "2022-05-30T21:19:41.543715",
        "modified": "2022-05-30T21:19:41.543715",
        "modifiedBy": 1,
        "id": 3,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "testes",
        "createdBy": 1,
        "created": "2022-05-30T21:14:32.549051",
        "modified": "2022-05-30T21:14:32.549051",
        "modifiedBy": 1,
        "id": 1,
        "accessCardStatus": "IN_USE_AND_BLOCKED"
      },
      {
        "code": "asetase",
        "createdBy": 1,
        "created": "2022-05-30T21:17:53.578356",
        "modified": "2022-05-30T21:17:53.578356",
        "modifiedBy": 1,
        "id": 2,
        "accessCardStatus": "AVAILABLE"
      },

    ],
    "first": true,
    "totalElements": 49,
    "empty": false
  },
  "success": true
}

const badgerMapper = (accessCardStatus: AccessCardStatus) => {
  switch (accessCardStatus) {
    case 'AVAILABLE':
      return <EuiBadge color='success'>Disponível</EuiBadge>
    case 'IN_USE':
      return <EuiBadge color='warning'>Em uso</EuiBadge>
    case 'BLOCKED':
      return <EuiBadge color='danger'>Bloqueado</EuiBadge>
    case 'CANCELLED':
      return <EuiBadge color='default'>Cancelado</EuiBadge>
    case 'IN_USE_AND_BLOCKED':
      return <EuiBadge color='danger'>Em uso e bloqueado</EuiBadge>
  }
}

const renderFirst = (item: Card, enabled: boolean) => {
  if (item.accessCardStatus === 'AVAILABLE' || item.accessCardStatus === 'IN_USE') {
    return (
      <EuiLink color="danger">Bloquear</EuiLink>
    );
  }
  else if (item.accessCardStatus === 'BLOCKED' || item.accessCardStatus === 'IN_USE_AND_BLOCKED') {
    return (
      <EuiLink color="success">Desbloquear</EuiLink>
    );
  }
  else if (item.accessCardStatus === 'CANCELLED') {
    return (
      <EuiLink color="success">Recuperar</EuiLink>
    );
  }
  return <></>;
}

const renderSecond = (item: Card, enabled: boolean) => {
  if (item.accessCardStatus === 'CANCELLED') {
    return (
      <EuiLink color="danger">Excluir</EuiLink>
    );
  }
  return <EuiLink color="danger">Cancelar</EuiLink>;
}

export default () => {
  return (
    <div style={{
      padding: 300
    }}>
      <EuiForm>
        <EuiFlexGroup>
          <EuiFlexItem>
            <EuiFieldSearch
              fullWidth
              placeholder='Código do cartão'
              append={<EuiButtonEmpty >
                <EuiSelect options={[
                  {
                    value: 'any',
                    text: 'Qualquer'
                  },
                  {
                    value: 'available',
                    text: 'Disponível'
                  },
                  {
                    value: 'inUse',
                    text: 'Em uso'
                  },
                  {
                    value: 'blocked',
                    text: 'Bloqueado'
                  },
                  {
                    value: 'inUseBlocked',
                    text: 'Em uso e bloqueado'
                  },
                  {
                    value: 'cancelled',
                    text: 'Cancelado'
                  },
                ]}
                />
              </EuiButtonEmpty>
              }
            />
          </EuiFlexItem>
          <EuiFlexItem grow={false}>
            <EuiButton>Filtrar</EuiButton>
          </EuiFlexItem>
          <EuiSpacer></EuiSpacer>
        </EuiFlexGroup>
      </EuiForm>
      <EuiSpacer></EuiSpacer>
      <EuiBasicTable
        pagination={{
          pageIndex: response.search.pageable.pageNumber,
          pageSize: response.search.pageable.pageSize,
          totalItemCount: response.search.totalElements
        }}
        onChange={() => {}}
        items={response.search.content}
        columns={[
          {
            field: 'code',
            name: 'Código',
          },
          {
            field: 'accessCardStatus',
            name: 'Estado',
            render: (value: AccessCardStatus, card: Card) => badgerMapper(value)
          },
          {
            name: 'Ações',
            actions: [
              {
                render: renderFirst
              },
              {
                render: renderSecond
              }
            ]
          }
        ]}
      >

      </EuiBasicTable>
    </div>
  );
};