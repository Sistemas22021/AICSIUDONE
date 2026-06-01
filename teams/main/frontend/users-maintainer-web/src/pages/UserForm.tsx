import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../services/api';
import toast from 'react-hot-toast';
import { Upload, ArrowLeft, Save } from 'lucide-react';

export const UserForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    fullName: '',
  });
  
  const [photoFile, setPhotoFile] = useState<File | null>(null);
  const [photoPreview, setPhotoPreview] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isEdit) {
      // In a real app we'd fetch the specific user details here
      // For this example we'll just show the toast and pretend
      toast.custom(<div>Editando... cargando datos...</div>, { duration: 1000});
    }
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setPhotoFile(file);
      setPhotoPreview(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      if (isEdit) {
        // En edición solo pasamos fullName
        await api.put(`/users/${id}`, { fullName: formData.fullName });
        toast.success("Usuario actualizado correctamente, se registró auditoría.");
        navigate('/users');
      } else {
        if (!photoFile) {
          toast.error("La foto de perfil de cara es obligatoria para nuevos usuarios.");
          setLoading(false);
          return;
        }

        const data = new FormData();
        data.append('username', formData.username);
        data.append('email', formData.email);
        data.append('fullName', formData.fullName);
        data.append('photo', photoFile);

        await api.post('/users', data, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        toast.success("Usuario creado. Se envió email con credenciales temporales.");
        navigate('/users');
      }
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div className="flex items-center gap-4 border-b border-slate-100 pb-4">
        <button onClick={() => navigate('/users')} className="p-2 bg-slate-100 text-slate-600 rounded-full hover:bg-slate-200 transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h2 className="text-2xl font-bold text-slate-800">
          {isEdit ? 'Editar Usuario' : 'Crear Nuevo Usuario'}
        </h2>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        
        {/* Subida de foto (solo creación) */}
        {!isEdit && (
          <div className="flex flex-col items-center gap-4 p-6 bg-slate-50 border-2 border-dashed border-slate-200 rounded-2xl">
            <div className="relative group cursor-pointer">
              <div className="w-32 h-32 rounded-full overflow-hidden bg-slate-200 border-4 border-white shadow-lg mx-auto flex items-center justify-center">
                {photoPreview ? (
                  <img src={photoPreview} alt="Preview" className="w-full h-full object-cover" />
                ) : (
                  <Upload className="w-10 h-10 text-slate-400 group-hover:text-indigo-500 transition-colors" />
                )}
              </div>
              <input type="file" required={!isEdit} accept="image/*" onChange={handlePhotoChange} className="absolute inset-0 opacity-0 cursor-pointer" />
            </div>
            <div className="text-center text-sm text-slate-500">
              <span className="font-medium text-indigo-600">Sube una foto de perfil</span> (Obligatorio) <br />
              Se recomienda foto clara del rostro
            </div>
          </div>
        )}

        <div className="grid grid-cols-1 gap-6">
          {!isEdit && (
            <>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nombre de Usuario (Username)</label>
                <input type="text" name="username" required value={formData.username} onChange={handleChange} 
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 outline-none transition-all" />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Correo Electrónico</label>
                <input type="email" name="email" required value={formData.email} onChange={handleChange} 
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 outline-none transition-all" />
              </div>
            </>
          )}

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Nombre Completo</label>
            <input type="text" name="fullName" required value={formData.fullName} onChange={handleChange} 
              className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 outline-none transition-all" />
          </div>
        </div>

        <div className="pt-4 flex justify-end">
          <button type="submit" disabled={loading} className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-xl font-medium shadow-md shadow-indigo-200 hover:shadow-lg hover:shadow-indigo-300 transition-all flex items-center gap-2">
            {loading ? 'Guardando...' : <><Save className="w-5 h-5"/> {isEdit ? 'Actualizar Usuario' : 'Crear Usuario'}</>}
          </button>
        </div>
      </form>
    </div>
  );
};
