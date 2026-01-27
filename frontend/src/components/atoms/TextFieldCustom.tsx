import { styled, TextField, type TextFieldProps } from '@mui/material';

const StyledTextField = styled(TextField)(({ theme }) => ({
  '& .MuiOutlinedInput-root': {
    borderRadius: theme.shape.borderRadius,
    transition: theme.transitions.create(['border-color', 'box-shadow']),

    '& fieldset': {
      borderColor: theme.palette.divider,
    },
    '&:hover fieldset': {
      borderColor: theme.palette.primary.main,
    },
    '&.Mui-focused fieldset': {
      borderWidth: '2px',
      borderColor: theme.palette.primary.main,
    },
  },
  '& .MuiInputLabel-root': {
    color: theme.palette.text.secondary,
    '&.Mui-focused': {
      color: theme.palette.primary.main,
    },
  },
}));

export const TextFieldCustom = (props: TextFieldProps) => {
  return (
    <StyledTextField
      {...props}
      fullWidth
      variant="outlined"
      size="medium"
      slotProps={{
        inputLabel: {
          shrink: true,
        },
      }}
    />
  );
};
