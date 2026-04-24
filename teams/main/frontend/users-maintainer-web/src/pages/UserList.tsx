import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { Link } from 'react-router-dom';
import Swal from 'sweetalert2';
import withReactContent from 'sweetalert2-react-content';
import { Edit2, Trash2, Search, UserPlus, Mail, ShieldAlert } from 'lucide-react';
import toast from 'react-hot-toast';

const MySwal = withReactContent(Swal);

export const UserList = () => {
  const [users, setUsers] = useState<any[]>([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await api.get('/users', { params: { search, page, size: 10 } });
      setUsers(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page]);

  useEffect(() => {
    const delayDebounce = setTimeout(() => {
      setPage(0);
      fetchUsers();
    }, 500);
    return () => clearTimeout(delayDebounce);
  }, [search]);

  const handleDelete = (id: string, name: string) => {
    MySwal.fire({
      title: '¿Dar de baja al usuario?',
      html: `Estás a punto de dar de baja a <strong>${name}</strong>.<br/><br/>¿Qué tipo de baja deseas aplicar?`,
      icon: 'warning',
      showCancelButton: true,
      showDenyButton: true,
      confirmButtonText: 'Baja Temporal',
      denyButtonText: 'Baja Permanente',
      cancelButtonText: 'Cancelar',
      customClass: {
        confirmButton: 'bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded',
        denyButton: 'bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded',
        cancelButton: 'bg-slate-300 hover:bg-slate-400 text-slate-800 font-bold py-2 px-4 rounded'
      },
      buttonsStyling: false
    }).then(async (result) => {
      if (result.isConfirmed || result.isDenied) {
        const permanent = result.isDenied;
        try {
          await api.delete(`/users/${id}?permanent=${permanent}`);
          toast.success(`Usuario dado de baja ${permanent ? 'permanentemente' : 'temporalmente'}`);
          fetchUsers();
        } catch (error) {
          console.error(error);
        }
      }
    });
  };

  const handleResetPassword = async (id: string) => {
    try {
      await api.post(`/users/${id}/reset-password`);
      toast.success('Correo de reseteo de contraseña enviado.');
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h2 className="text-2xl font-bold text-slate-800 flex items-center gap-2">
          Gestión de Usuarios
        </h2>
        <Link to="/users/new" className="bg-indigo-600 hover:bg-indigo-700 text-white px-5 py-2.5 rounded-xl font-medium shadow-md shadow-indigo-200 transition-all flex items-center gap-2">
          <UserPlus className="w-5 h-5" /> Nuevo Usuario
        </Link>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 w-5 h-5" />
        <input 
          type="text" 
          placeholder="Buscar por nombre o username..." 
          className="w-full pl-10 pr-4 py-3 rounded-xl border border-slate-200 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 outline-none transition-all shadow-sm"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-600">
            <thead className="text-xs text-slate-500 uppercase bg-slate-50 border-b border-slate-100">
              <tr>
                <th className="px-6 py-4 font-medium">Usuario</th>
                <th className="px-6 py-4 font-medium">Username</th>
                <th className="px-6 py-4 font-medium">Estado</th>
                <th className="px-6 py-4 font-medium text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr><td colSpan={4} className="text-center py-10">Cargando...</td></tr>
              ) : users.length === 0 ? (
                <tr><td colSpan={4} className="text-center py-10 text-slate-400">No se encontraron usuarios.</td></tr>
              ) : (
                users.map(user => (
                  <tr key={user.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        {user.profilePhotoUrl ? (
                          <img src={'http://localhost:8085'+user.profilePhotoUrl} alt="avatar" className="w-10 h-10 rounded-full object-cover border border-slate-200" />
                        ) : (
                          <div className="w-10 h-10 rounded-full bg-slate-200 flex items-center justify-center font-bold text-slate-500">
                            {user.fullName.charAt(0)}
                          </div>
                        )}
                        <div>
                          <div className="font-semibold text-slate-800">{user.fullName}</div>
                          <div className="text-xs text-slate-500 flex items-center gap-1"><Mail className="w-3 h-3"/> {user.email}</div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 font-medium text-slate-700">{user.username}</td>
                    <td className="px-6 py-4">
                      <span className={\`px-2.5 py-1 rounded-full text-xs font-semibold \${ user.status === 'ACTIVE' ? 'bg-emerald-100 text-emerald-700' : 'bg-yellow-100 text-yellow-700' }\`}>
                        {user.status === 'ACTIVE' ? 'Activo' : 'Baja Temporal'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex justify-end gap-2">
                        <button onClick={() => handleResetPassword(user.id)} title="Reset Password" className="p-2 text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                          <ShieldAlert className="w-4 h-4" />
                        </button>
                        <Link to={\`/users/edit/\${user.id}\`} title="Editar" className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                          <Edit2 className="w-4 h-4" />
                        </Link>
                        <button onClick={() => handleDelete(user.id, user.fullName)} title="Eliminar" className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        
        {/* Pagination */ }
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-slate-100 bg-slate-50">
            <button 
              disabled={page === 0} 
              onClick={() => setPage(p => p - 1)}
              className="px-4 py-2 text-sm font-medium text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed">
              Anterior
            </button>
            <span className="text-sm text-slate-500">Página {page + 1} de {totalPages}</span>
            <button 
              disabled={page >= totalPages - 1} 
              onClick={() => setPage(p => p + 1)}
              className="px-4 py-2 text-sm font-medium text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed">
              Siguiente
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
