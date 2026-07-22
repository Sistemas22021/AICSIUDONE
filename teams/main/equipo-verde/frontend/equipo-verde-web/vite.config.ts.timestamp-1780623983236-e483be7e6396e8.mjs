// vite.config.ts
import { defineConfig } from "file:///C:/Users/crist/OneDrive/Documents/SISTEMAS%202%20PROYECTO/back-ev/teams/main/equipo-verde/frontend/equipo-verde-web/node_modules/.pnpm/vite@5.4.21_@types+node@25.9.1_lightningcss@1.32.0/node_modules/vite/dist/node/index.js";
import react from "file:///C:/Users/crist/OneDrive/Documents/SISTEMAS%202%20PROYECTO/back-ev/teams/main/equipo-verde/frontend/equipo-verde-web/node_modules/.pnpm/@vitejs+plugin-react@4.7.0__3cd27e3c657bdfeea809589eacb59f1e/node_modules/@vitejs/plugin-react/dist/index.js";
import tailwindcss from "file:///C:/Users/crist/OneDrive/Documents/SISTEMAS%202%20PROYECTO/back-ev/teams/main/equipo-verde/frontend/equipo-verde-web/node_modules/.pnpm/@tailwindcss+vite@4.3.0_vit_92ad0bee4c5dbf6ae87e4792187a824a/node_modules/@tailwindcss/vite/dist/index.mjs";
var vite_config_default = defineConfig({
  plugins: [
    tailwindcss(),
    react()
  ],
  server: {
    port: 3002,
    proxy: {
      "/api": {
        // En local apuntamos directo al balistic-services (sin API Gateway)
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: "dist",
    sourcemap: true
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJDOlxcXFxVc2Vyc1xcXFxjcmlzdFxcXFxPbmVEcml2ZVxcXFxEb2N1bWVudHNcXFxcU0lTVEVNQVMgMiBQUk9ZRUNUT1xcXFxiYWNrLWV2XFxcXHRlYW1zXFxcXG1haW5cXFxcZXF1aXBvLXZlcmRlXFxcXGZyb250ZW5kXFxcXGVxdWlwby12ZXJkZS13ZWJcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZmlsZW5hbWUgPSBcIkM6XFxcXFVzZXJzXFxcXGNyaXN0XFxcXE9uZURyaXZlXFxcXERvY3VtZW50c1xcXFxTSVNURU1BUyAyIFBST1lFQ1RPXFxcXGJhY2stZXZcXFxcdGVhbXNcXFxcbWFpblxcXFxlcXVpcG8tdmVyZGVcXFxcZnJvbnRlbmRcXFxcZXF1aXBvLXZlcmRlLXdlYlxcXFx2aXRlLmNvbmZpZy50c1wiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9pbXBvcnRfbWV0YV91cmwgPSBcImZpbGU6Ly8vQzovVXNlcnMvY3Jpc3QvT25lRHJpdmUvRG9jdW1lbnRzL1NJU1RFTUFTJTIwMiUyMFBST1lFQ1RPL2JhY2stZXYvdGVhbXMvbWFpbi9lcXVpcG8tdmVyZGUvZnJvbnRlbmQvZXF1aXBvLXZlcmRlLXdlYi92aXRlLmNvbmZpZy50c1wiO2ltcG9ydCB7IGRlZmluZUNvbmZpZyB9IGZyb20gJ3ZpdGUnO1xuaW1wb3J0IHJlYWN0IGZyb20gJ0B2aXRlanMvcGx1Z2luLXJlYWN0JztcbmltcG9ydCB0YWlsd2luZGNzcyBmcm9tICdAdGFpbHdpbmRjc3Mvdml0ZSc7XG5cbmV4cG9ydCBkZWZhdWx0IGRlZmluZUNvbmZpZyh7XG4gIHBsdWdpbnM6IFtcbiAgICB0YWlsd2luZGNzcygpLFxuICAgIHJlYWN0KCksXG4gIF0sXG4gIHNlcnZlcjoge1xuICAgIHBvcnQ6IDMwMDIsXG4gICAgcHJveHk6IHtcbiAgICAgICcvYXBpJzoge1xuICAgICAgICAvLyBFbiBsb2NhbCBhcHVudGFtb3MgZGlyZWN0byBhbCBiYWxpc3RpYy1zZXJ2aWNlcyAoc2luIEFQSSBHYXRld2F5KVxuICAgICAgICB0YXJnZXQ6ICdodHRwOi8vbG9jYWxob3N0OjgwODAnLFxuICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWUsXG4gICAgICB9LFxuICAgIH0sXG4gIH0sXG4gIGJ1aWxkOiB7XG4gICAgb3V0RGlyOiAnZGlzdCcsXG4gICAgc291cmNlbWFwOiB0cnVlLFxuICB9LFxufSk7XG4iXSwKICAibWFwcGluZ3MiOiAiO0FBQTJoQixTQUFTLG9CQUFvQjtBQUN4akIsT0FBTyxXQUFXO0FBQ2xCLE9BQU8saUJBQWlCO0FBRXhCLElBQU8sc0JBQVEsYUFBYTtBQUFBLEVBQzFCLFNBQVM7QUFBQSxJQUNQLFlBQVk7QUFBQSxJQUNaLE1BQU07QUFBQSxFQUNSO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixPQUFPO0FBQUEsTUFDTCxRQUFRO0FBQUE7QUFBQSxRQUVOLFFBQVE7QUFBQSxRQUNSLGNBQWM7QUFBQSxNQUNoQjtBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQUEsRUFDQSxPQUFPO0FBQUEsSUFDTCxRQUFRO0FBQUEsSUFDUixXQUFXO0FBQUEsRUFDYjtBQUNGLENBQUM7IiwKICAibmFtZXMiOiBbXQp9Cg==
