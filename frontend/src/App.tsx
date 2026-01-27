import './App.css';
import { router } from './routes';
import { RouterProvider } from 'react-router-dom';
import { LanguageProvider, ThemeContextProvider } from './context';

function App() {
  return (
    <ThemeContextProvider>
      <LanguageProvider>
        <RouterProvider router={router} />
      </LanguageProvider>
    </ThemeContextProvider>
  );
}

export default App;
