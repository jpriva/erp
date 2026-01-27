import { Box, Container, Paper, useTheme } from '@mui/material';
import { Outlet } from 'react-router-dom';
import { SettingsBar } from '@components/organisms';

export const AuthLayout = () => {
  const theme = useTheme();

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        bgcolor: theme.palette.background.default,
        transition: 'background-color 0.3s ease',
      }}
    >
      <SettingsBar />

      <Container
        maxWidth="sm"
        sx={{
          flex: 1,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          pb: 8,
        }}
      >
        <Paper
          elevation={theme.palette.mode === 'dark' ? 8 : 4}
          variant="elevation"
          sx={{
            p: { xs: 3, md: 6 },
            width: '100%',
            borderRadius: theme.shape.borderRadius,
            bgcolor: theme.palette.background.paper,
            border: theme.palette.mode === 'light' ? '1px solid #dcd7ba' : 'none',
          }}
        >
          <Outlet />
        </Paper>
      </Container>
    </Box>
  );
};
