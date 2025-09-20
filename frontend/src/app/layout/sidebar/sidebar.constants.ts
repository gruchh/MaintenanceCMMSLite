import {
  heroHome,
  heroCog6Tooth,
  heroCubeTransparent,
  heroUsers,
  heroLifebuoy,
  heroArrowLeftOnRectangle,
  heroBars3,
  heroXMark,
  heroCake,
  heroCalendarDateRange,
} from '@ng-icons/heroicons/outline';

export interface MenuItem {
  path: string;
  iconName: string;
  label: string;
}

export interface ActionItem {
  action: () => void;
  iconName: string;
  label: string;
}

export const MENU_ITEMS: MenuItem[] = [
  { path: '/', iconName: 'heroHome', label: 'Strona Główna' },
  {
    path: '/dashboard',
    iconName: 'heroCog6Tooth',
    label: 'Dashboard',
  },
  {
    path: '/dashboard/breakdowns',
    iconName: 'heroCake',
    label: 'Awarie',
  },
  {
    path: '/dashboard/spare-parts',
    iconName: 'heroCubeTransparent',
    label: 'Części zamienne',
  },
  {
    path: '/dashboard/employees',
    iconName: 'heroUsers',
    label: 'Pracownicy',
  },
  {
    path: '/dashboard/work-schedule',
    iconName: 'heroCalendarDateRange',
    label: 'Harmonogram',
  },
];

export const SIDEBAR_ICONS = {
  heroHome,
  heroCog6Tooth,
  heroCubeTransparent,
  heroUsers,
  heroLifebuoy,
  heroArrowLeftOnRectangle,
  heroBars3,
  heroXMark,
  heroCake,
  heroCalendarDateRange,
};
