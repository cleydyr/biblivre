import { Route, Routes } from 'react-router-dom'

import BibliographicSearchPage from './search/BibliographicSearchPage'

const App = () => {
  return (
    <Routes>
      <Route element={<BibliographicSearchPage />} path='/search' />
    </Routes>
  )
}

export default App
