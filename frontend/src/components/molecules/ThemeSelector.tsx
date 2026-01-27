import { IconButton, useTheme } from '@mui/material';
import { useAppTheme } from '@/hooks';
import { Brightness4, Brightness7 } from '@mui/icons-material';

export const ThemeSelector = () => {
  const { themeName, setThemeName } = useAppTheme();
  const theme = useTheme();

  return (
    <IconButton
      onClick={() => setThemeName(themeName === 'light' ? 'dark' : 'light')}
      sx={{ color: theme.palette.text.primary }}
    >
      {themeName === 'light' ? <Brightness7 /> : <Brightness4 />}
    </IconButton>
  );
};
