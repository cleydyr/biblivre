import type { SVGProps } from 'react'

type PrinterIconProps = SVGProps<SVGSVGElement> & {
  title?: string
  titleId?: string
}

const PrinterIcon = ({ title, titleId, ...props }: PrinterIconProps) => (
  <svg
    aria-labelledby={titleId}
    height={16}
    viewBox='0 0 16 16'
    width={16}
    xmlns='http://www.w3.org/2000/svg'
    {...props}
  >
    {title ? <title id={titleId}>{title}</title> : null}
    <path
      clipRule='evenodd'
      d='M5 1h6v3H5V1zm1 1v1h4V2H6zM2 5h12v6H2V5zm2 1h2v2H4V6zM4 12h8v3H4v-3zm1 1v1h6v-1H5z'
      fillRule='evenodd'
    />
  </svg>
)

export default PrinterIcon
