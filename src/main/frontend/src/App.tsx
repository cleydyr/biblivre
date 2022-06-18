
import '@elastic/eui/dist/eui_theme_light.css';
import { useState } from 'react';
import AccessCardScreen from './screens/administration/access_card/AccessCardScreen';
import { AccessCard, AccessCardStatus } from './entity/access_card/AccessCard';
import { SearchResponse } from './Response';
import { ReponseLaxedTypeTransformer, ResponseLaxedType } from './ResponseLaxedType';

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

const App = () => {
  const [items, setItems] = useState([] as AccessCard[]);

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