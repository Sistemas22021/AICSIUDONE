import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { 
  Box, 
  Drawer, 
  List, 
  ListItem, 
  ListItemButton, 
  ListItemIcon, 
  ListItemText, 
  AppBar, 
  Toolbar, 
  IconButton, 
  Typography,
  useTheme,
  useMediaQuery
} from '@mui/material';
import { 
  Menu as MenuIcon, 
  PlusCircle, 
  Search, 
  ShieldCheck,
  FolderOpen
} from 'lucide-react';

const drawerWidth = 260;

interface DashboardLayoutProps {
  children: React.ReactNode;
}

export const DashboardLayout = ({ children }: DashboardLayoutProps) => {
  const [open, setOpen] = useState(true);
  const location = useLocation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const toggleDrawer = () => {
    setOpen(!open);
  };

  const menuItems = [
    { text: 'Registro Evidencia', icon: <PlusCircle size={20} />, path: '/' },
    { text: 'Gestión Expedientes', icon: <FolderOpen size={20} />, path: '/expedientes' },
    { text: 'Motor Correlación', icon: <Search size={20} />, path: '/correlacion' },
    { text: 'Cadena de Custodia', icon: <ShieldCheck size={20} />, path: '/auditoria' },
  ];

  return (
    <Box sx={{ display: 'flex', bgcolor: '#f8fafc', minHeight: '100vh' }}>
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          zIndex: (theme) => theme.zIndex.drawer + 1,
          bgcolor: 'white',
          borderBottom: '1px solid #e2e8f0',
          color: '#1e293b'
        }}
      >
        <Toolbar className="flex justify-between">
          <Box className="flex items-center">
            <IconButton
              color="inherit"
              aria-label="open drawer"
              onClick={toggleDrawer}
              edge="start"
              sx={{ mr: 2 }}
            >
              <MenuIcon size={24} />
            </IconButton>
            <Typography variant="h6" noWrap component="div" className="font-extrabold tracking-tight flex items-center gap-2">
              <Box className="bg-slate-900 text-white p-1 rounded">
                <ShieldCheck size={20} />
              </Box>
              SIS-BALISTICA <span className="text-slate-400 font-medium ml-2 text-sm border-l pl-2 border-slate-200">DIVISIÓN FORENSE</span>
            </Typography>
          </Box>
          <Box className="flex items-center gap-4">
            <Box className="text-right">
              <Typography variant="body2" className="font-bold text-slate-900">Perito A. Córdoba</Typography>
              <Typography variant="caption" className="text-slate-500">ID: 8829-2026</Typography>
            </Box>
            <Box className="w-10 h-10 bg-slate-200 rounded-full flex items-center justify-center text-slate-600 font-bold">
              AC
            </Box>
          </Box>
        </Toolbar>
      </AppBar>
      <Drawer
        variant={isMobile ? 'temporary' : 'permanent'}
        open={open}
        onClose={toggleDrawer}
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: { 
            width: drawerWidth, 
            boxSizing: 'border-box',
            borderRight: '1px solid #e2e8f0',
            bgcolor: 'white',
            transition: theme.transitions.create('width', {
              easing: theme.transitions.easing.sharp,
              duration: theme.transitions.duration.enteringScreen,
            }),
            ...(!open && {
              width: 70,
              overflowX: 'hidden',
            })
          },
        }}
      >
        <Toolbar />
        <Box sx={{ overflow: 'auto', mt: 2 }}>
          <List>
            {menuItems.map((item) => {
              const isActive = location.pathname === item.path;
              return (
                <ListItem key={item.text} disablePadding sx={{ display: 'block', px: 1.5, mb: 0.5 }}>
                  <ListItemButton
                    component={Link}
                    to={item.path}
                    sx={{
                      minHeight: 48,
                      justifyContent: open ? 'initial' : 'center',
                      px: 2.5,
                      borderRadius: '8px',
                      bgcolor: isActive ? '#0f172a' : 'transparent',
                      color: isActive ? 'white' : '#475569',
                      '&:hover': {
                        bgcolor: isActive ? '#1e293b' : '#f8fafc',
                        color: isActive ? 'white' : '#0f172a',
                      },
                    }}
                  >
                    <ListItemIcon
                      sx={{
                        minWidth: 0,
                        mr: open ? 2 : 'auto',
                        justifyContent: 'center',
                        color: 'inherit'
                      }}
                    >
                      {item.icon}
                    </ListItemIcon>
                    <ListItemText 
                      primary={item.text} 
                      sx={{ opacity: open ? 1 : 0 }} 
                      slotProps={{
                        primary: {
                          sx: { 
                            fontSize: '14px', 
                            fontWeight: isActive ? 600 : 500 
                          }
                        }
                      }}
                    />
                  </ListItemButton>
                </ListItem>
              );
            })}
          </List>
        </Box>
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, p: 4, mt: 8 }}>
        {children}
      </Box>
    </Box>
  );
};
