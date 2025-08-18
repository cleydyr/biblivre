declare module '@elastic/eui/es/components/icon/*'

declare module '@elastic/eui/es/components/icon/assets/*' {
  import type { SVGProps } from 'react'

  interface SVGRProps {
    title?: string
    titleId?: string
  }
  export const icon: ({
    title,
    titleId,
    ...props
  }: SVGProps<SVGSVGElement> & SVGRProps) => JSX.Element
  export {}
}
