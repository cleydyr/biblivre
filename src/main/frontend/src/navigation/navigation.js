import React from 'react';
import {ClayVerticalNav} from '@clayui/nav';

import spritemap from "@clayui/css/lib/images/icons/icons.svg";

const Navigation = () => {
  return (
    <ClayVerticalNav
      activeLabel="Five"
      items={[
        {
          initialExpanded: true,
          items: [
            {
              href: "#nested1",
              label: "Nested1"
            }
          ],
          label: "Home"
        },
        {
          href: "#2",
          label: "About"
        },
        {
          href: "#3",
          label: "Contact"
        },
        {
          items: [
            {
              active: true,
              href: "#5",
              label: "Five"
            },
            {
              href: "#6",
              label: "Six"
            }
          ],
          label: "Projects"
        },
        {
          href: "#7",
          label: "Seven"
        }
      ]}
      large={false}
      spritemap={spritemap}
    />
  );
};

export default Navigation;