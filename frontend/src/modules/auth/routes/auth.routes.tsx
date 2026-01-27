import type { RouteObject } from 'react-router-dom';
import { AuthLayout } from '../components/templates';
import { LoginPage } from '../components/pages';

export const authRoutes: RouteObject[] = [
  {
    path: '/auth',
    element: <AuthLayout />,
    children: [
      {
        path: '/auth/login',
        element: <LoginPage />,
      },
    ],
  },
];
