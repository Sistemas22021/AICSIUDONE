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
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJDOlxcXFxVc2Vyc1xcXFxjcmlzdFxcXFxPbmVEcml2ZVxcXFxEb2N1bWVudHNcXFxcU0lTVEVNQVMgMiBQUk9ZRUNUT1xcXFxiYWNrLWV2XFxcXHRlYW1zXFxcXG1haW5cXFxcZXF1aXBvLXZlcmRlXFxcXGZyb250ZW5kXFxcXGVxdWlwby12ZXJkZS13ZWJcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZmlsZW5hbWUgPSBcIkM6XFxcXFVzZXJzXFxcXGNyaXN0XFxcXE9uZURyaXZlXFxcXERvY3VtZW50c1xcXFxTSVNURU1BUyAyIFBST1lFQ1RPXFxcXGJhY2stZXZcXFxcdGVhbXNcXFxcbWFpblxcXFxlcXVpcG8tdmVyZGVcXFxcZnJvbnRlbmRcXFxcZXF1aXBvLXZlcmRlLXdlYlxcXFx2aXRlLmNvbmZpZy50c1wiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9pbXBvcnRfbWV0YV91cmwgPSBcImZpbGU6Ly8vQzovVXNlcnMvY3Jpc3QvT25lRHJpdmUvRG9jdW1lbnRzL1NJU1RFTUFTJTIwMiUyMFBST1lFQ1RPL2JhY2stZXYvdGVhbXMvbWFpbi9lcXVpcG8tdmVyZGUvZnJvbnRlbmQvZXF1aXBvLXZlcmRlLXdlYi92aXRlLmNvbmZpZy50c1wiO2ltcG9ydCB7IGRlZmluZUNvbmZpZyB9IGZyb20gJ3ZpdGUnO1xyXG5pbXBvcnQgcmVhY3QgZnJvbSAnQHZpdGVqcy9wbHVnaW4tcmVhY3QnO1xyXG5pbXBvcnQgdGFpbHdpbmRjc3MgZnJvbSAnQHRhaWx3aW5kY3NzL3ZpdGUnO1xyXG5cclxuZXhwb3J0IGRlZmF1bHQgZGVmaW5lQ29uZmlnKHtcclxuICBwbHVnaW5zOiBbXHJcbiAgICB0YWlsd2luZGNzcygpLFxyXG4gICAgcmVhY3QoKSxcclxuICBdLFxyXG4gIHNlcnZlcjoge1xyXG4gICAgcG9ydDogMzAwMixcclxuICAgIHByb3h5OiB7XHJcbiAgICAgICcvYXBpJzoge1xyXG4gICAgICAgIC8vIEVuIGxvY2FsIGFwdW50YW1vcyBkaXJlY3RvIGFsIGJhbGlzdGljLXNlcnZpY2VzIChzaW4gQVBJIEdhdGV3YXkpXHJcbiAgICAgICAgdGFyZ2V0OiAnaHR0cDovL2xvY2FsaG9zdDo4MDgwJyxcclxuICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWUsXHJcbiAgICAgIH0sXHJcbiAgICB9LFxyXG4gIH0sXHJcbiAgYnVpbGQ6IHtcclxuICAgIG91dERpcjogJ2Rpc3QnLFxyXG4gICAgc291cmNlbWFwOiB0cnVlLFxyXG4gIH0sXHJcbn0pO1xyXG4iXSwKICAibWFwcGluZ3MiOiAiO0FBQTJoQixTQUFTLG9CQUFvQjtBQUN4akIsT0FBTyxXQUFXO0FBQ2xCLE9BQU8saUJBQWlCO0FBRXhCLElBQU8sc0JBQVEsYUFBYTtBQUFBLEVBQzFCLFNBQVM7QUFBQSxJQUNQLFlBQVk7QUFBQSxJQUNaLE1BQU07QUFBQSxFQUNSO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixPQUFPO0FBQUEsTUFDTCxRQUFRO0FBQUE7QUFBQSxRQUVOLFFBQVE7QUFBQSxRQUNSLGNBQWM7QUFBQSxNQUNoQjtBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQUEsRUFDQSxPQUFPO0FBQUEsSUFDTCxRQUFRO0FBQUEsSUFDUixXQUFXO0FBQUEsRUFDYjtBQUNGLENBQUM7IiwKICAibmFtZXMiOiBbXQp9Cg==
