import useAdministrationNavigation from './administration'
import useSearchNavigation from './search'

const useNavigationItems = () => {
  return [useSearchNavigation(), useAdministrationNavigation()]
}

export default useNavigationItems
