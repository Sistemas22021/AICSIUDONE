/**
 * Validaciones de formatos venezolanos.
 * Espejo de las validaciones del backend para mejor UX (validar antes de enviar).
 */

// Cedula: V o E opcional, guion opcional, 6-8 digitos
const CEDULA_REGEX = /^([VvEe]-?)?\d{6,8}$/;

// Placa: AB123CD (2L 3N 2L) o formatos viejos
const PLACA_REGEX = /^([A-Za-z]{2}\d{3}[A-Za-z]{2}|[A-Za-z]{3}\d{2}[A-Za-z]|[A-Za-z]{3}\d{3})$/;

// Telefono: codigo de area valido + 7 digitos
const TELEFONO_REGEX = /^(0?(212|234|235|238|239|240|241|242|243|244|245|246|247|248|249|251|252|253|254|255|256|257|258|259|260|261|262|263|264|265|266|267|268|269|271|272|273|274|275|276|277|278|279|281|282|283|284|285|286|287|288|289|291|292|293|294|295|412|414|416|424|426))-?\d{7}$/;

// RIF
const RIF_REGEX = /^[JGVEPjgvep]-?\d{8,9}-?\d$/;

export const validaciones = {
  cedula: (valor: string): boolean => {
    if (!valor || valor.trim() === '') return true; // opcional
    return CEDULA_REGEX.test(valor.trim());
  },

  placa: (valor: string): boolean => {
    if (!valor || valor.trim() === '') return true;
    return PLACA_REGEX.test(valor.trim());
  },

  telefono: (valor: string): boolean => {
    if (!valor || valor.trim() === '') return true;
    const limpio = valor.trim().replace(/[\s()]/g, '');
    return TELEFONO_REGEX.test(limpio);
  },

  rif: (valor: string): boolean => {
    if (!valor || valor.trim() === '') return true;
    return RIF_REGEX.test(valor.trim());
  },
};

// Mensajes de error reutilizables
export const mensajesValidacion = {
  cedula: 'Cédula inválida (formato: V-12345678)',
  placa: 'Placa inválida (formato: AB123CD)',
  telefono: 'Teléfono inválido (formato: 0414-1234567)',
  rif: 'RIF inválido (formato: J-123456789)',
};