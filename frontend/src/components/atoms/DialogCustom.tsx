import { Dialog, styled } from '@mui/material';

export const StyledDialog = styled(Dialog)(({ theme }) => ({
  '& .MuiPaper-root': {
    borderRadius: 16,
    padding: theme.spacing(2),
    backgroundColor: theme.palette.background.paper,
    backgroundImage: 'none',
  },
}));
