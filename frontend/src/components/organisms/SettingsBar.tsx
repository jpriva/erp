import { Box, Stack } from '@mui/material';
import { LanguageSelector, ThemeSelector } from '../molecules';

export const SettingsBar = () => {
  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'flex-end',
        p: 2,
        gap: 2,
        bgcolor: 'transparent',
      }}
    >
      <Stack direction="row" spacing={2} alignItems="center">
        <LanguageSelector />
        <ThemeSelector />
      </Stack>
    </Box>
  );
};
