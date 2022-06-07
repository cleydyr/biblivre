
import '@elastic/eui/dist/eui_theme_light.css';
import { useState } from 'react';
import AccessCardScreen from './AccessCardScreen';
import { AccessCard, AccessCardStatus } from './Card';
import { SearchResponse } from './Response';
import { ReponseLaxedTypeTransformer, ResponseLaxedType } from './ResponseLaxedType';

const rawResponse: SearchResponse<ResponseLaxedType<AccessCard>> = {
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
        "id": 113,
        "accessCardStatus": "IN_USE"
      },
      {
        "code": "fawefqwef",
        "createdBy": 1,
        "created": "2022-05-30T21:35:09.536",
        "modified": "2022-05-30T21:35:09.536",
        "modifiedBy": 1,
        "id": 105,
        "accessCardStatus": "CANCELLED"
      },
      {
        "code": "teste",
        "createdBy": 1,
        "created": "2022-05-30T20:54:24.322",
        "modified": "2022-05-30T20:54:24.322",
        "modifiedBy": 1,
        "id": 106,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "asetasetas",
        "createdBy": 1,
        "created": "2022-05-30T21:25:25.162",
        "modified": "2022-05-30T21:25:25.162",
        "modifiedBy": 1,
        "id": 104,
        "accessCardStatus": "BLOCKED"
      },
      {
        "code": "fasefaseqw",
        "createdBy": 1,
        "created": "2022-05-30T21:19:41.543715",
        "modified": "2022-05-30T21:19:41.543715",
        "modifiedBy": 1,
        "id": 103,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "testes",
        "createdBy": 1,
        "created": "2022-05-30T21:14:32.549051",
        "modified": "2022-05-30T21:14:32.549051",
        "modifiedBy": 1,
        "id": 101,
        "accessCardStatus": "IN_USE_AND_BLOCKED"
      },
      {
        "code": "asetase",
        "createdBy": 1,
        "created": "2022-05-30T21:17:53.578356",
        "modified": "2022-05-30T21:17:53.578356",
        "modifiedBy": 1,
        "id": 102,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "asebbasase",
        "createdBy": 1,
        "created": "2022-05-31T08:19:51.662",
        "modified": "2022-05-31T08:19:51.662",
        "modifiedBy": 1,
        "id": 213,
        "accessCardStatus": "IN_USE"
      },
      {
        "code": "fawefqwef",
        "createdBy": 1,
        "created": "2022-05-30T21:35:09.536",
        "modified": "2022-05-30T21:35:09.536",
        "modifiedBy": 1,
        "id": 205,
        "accessCardStatus": "CANCELLED"
      },
      {
        "code": "teste",
        "createdBy": 1,
        "created": "2022-05-30T20:54:24.322",
        "modified": "2022-05-30T20:54:24.322",
        "modifiedBy": 1,
        "id": 206,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "asetasetas",
        "createdBy": 1,
        "created": "2022-05-30T21:25:25.162",
        "modified": "2022-05-30T21:25:25.162",
        "modifiedBy": 1,
        "id": 204,
        "accessCardStatus": "BLOCKED"
      },
      {
        "code": "fasefaseqw",
        "createdBy": 1,
        "created": "2022-05-30T21:19:41.543715",
        "modified": "2022-05-30T21:19:41.543715",
        "modifiedBy": 1,
        "id": 203,
        "accessCardStatus": "AVAILABLE"
      },
      {
        "code": "testes",
        "createdBy": 1,
        "created": "2022-05-30T21:14:32.549051",
        "modified": "2022-05-30T21:14:32.549051",
        "modifiedBy": 1,
        "id": 201,
        "accessCardStatus": "IN_USE_AND_BLOCKED"
      },
      {
        "code": "asetase",
        "createdBy": 1,
        "created": "2022-05-30T21:17:53.578356",
        "modified": "2022-05-30T21:17:53.578356",
        "modifiedBy": 1,
        "id": 202,
        "accessCardStatus": "AVAILABLE"
      },

    ],
    "first": true,
    "totalElements": 49,
    "empty": false
  },
  "success": true
}

function parseResponse<T extends object>(response: SearchResponse<ResponseLaxedType<T>>, f: (json: ResponseLaxedType<T>) => T): SearchResponse<T> {
  return {
    ...response,
    search: {
      ...response.search,
      content: response.search.content.map(f)
    }
  };
}

function identity<T>(val: T): T {
  return val
}

const laxedTransformer: ReponseLaxedTypeTransformer<AccessCard> = {
  code: identity,
  createdBy: identity,
  created: val => new Date(val),
  modified: val => new Date(val),
  modifiedBy: identity,
  id: identity,
  accessCardStatus: val => AccessCardStatus[val as keyof typeof AccessCardStatus]
}

function relaxedTransformer(responseLaxedObject: ResponseLaxedType<AccessCard>): AccessCard {
  return {
    code: laxedTransformer.code.call(null, responseLaxedObject.code),
    createdBy: laxedTransformer.createdBy.call(null, responseLaxedObject.createdBy),
    created: laxedTransformer.created.call(null, responseLaxedObject.created),
    modified: laxedTransformer.modified.call(null, responseLaxedObject.modified),
    modifiedBy: laxedTransformer.modifiedBy.call(null, responseLaxedObject.modifiedBy),
    id: laxedTransformer.id.call(null, responseLaxedObject.id),
    accessCardStatus: laxedTransformer.accessCardStatus.call(null, responseLaxedObject.accessCardStatus)
  }
}

export const response = parseResponse(rawResponse, relaxedTransformer)

const App = () => {
  const [items, setItems] = useState(response.search.content);

  const onItemDelete = (accessCard: AccessCard) => {
    const { id } = accessCard

    setItems(items.filter(item => item.id !== id))
  }

  const onItemChange = (accessCard: AccessCard) => {
    const { id, accessCardStatus } = accessCard

    setItems(items.map(item => item.id === id ? { ...item, accessCardStatus } : item))
  }

  return (
    <AccessCardScreen
      items={items}
      onItemChange={onItemChange}
      onItemDelete={onItemDelete}
    />
  );
}

export default App