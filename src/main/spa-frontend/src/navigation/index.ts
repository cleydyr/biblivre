import useAdministrationNavigation from './administration'
import useCirculationNavigation from './circulation'
import useSearchNavigation from './search'

const useNavigationItems = () => {
  return [
    useSearchNavigation(),
    useCirculationNavigation(),
    useAdministrationNavigation(),
  ]
}

export default useNavigationItems
