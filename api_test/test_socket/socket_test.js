import { io } from "socket.io-client";

// process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

const socket = io("http://localhost:3000", {
  transports: ["websocket"],
});

console.log("Connecting to socket...");

// ← ajouter ces handlers de debug
socket.on("connect_error", (err) => {
  console.error("❌ connect_error:", err.message, err);
});

socket.on("connect", () => {
  console.log("✅ connected", socket.id);
  socket.emit("join_playlist", "42");
});

socket.on("playlist_updated", (data) => {
  console.log(data);
});
