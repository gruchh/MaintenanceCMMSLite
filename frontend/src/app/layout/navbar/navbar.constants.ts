export interface NavLink {
  path: string;
  label: string;
}

export const NAV_LINKS: NavLink[] = [
  { path: '/report-breakdown', label: 'Zgłoś awarię' },
  { path: '/dashboard', label: 'Dashboard' },
];
