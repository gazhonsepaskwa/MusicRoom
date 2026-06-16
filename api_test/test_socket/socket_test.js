import { io } from "socket.io-client";

const socket = io("http://localhost:3000", {
  auth: {
    token:
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjMsInVzZXJuYW1lIjoiYXNxemF6cyIsImlhdCI6MTc4MTE3MjQ2OCwiZXhwIjoxNzgxMjU4ODY4fQ.RAN-UTrkVe2nEYXwguIliyHzwPC2CYbPo5lotPWdSEU",
  },
});

console.log("Connecting to socket...");

socket.on("connect_error", (err) => {
  console.error("❌ connect_error:", err.message, err);
});

socket.on("connect", () => {
  console.log("✅ connected", socket.id);
  socket.emit("join_playlist", "42");
});

socket.on("join_playlist", (data) => {
  console.log("✅ join_playlist socket:", data);
});

socket.on("friendrequest", (data) => {
  console.log("✅ join_playlist socket:", data);
});

socket.on("playlist_updated", (data) => {
  console.log("✅ playlist_updated socket:", data);
});

const socketbis = io("http://localhost:3000", {
  auth: {
    token: "token",
  },
});

console.log("Connecting to socket...");

socketbis.on("connect_error", (err) => {
  console.error("❌ connect_error:", err.message, err);
});

socketbis.on("connect", () => {
  console.log("✅ connected", socketbis.id);
  socketbis.emit("join_playlist", "42");
  console.log("Emitting add_music...");
  socketbis.emit("add_music", {
    playlistId: "42",
    songId: "3",
    version: 4,
  });

  socketbis.emit("move_music", {
    playlistId: "42",
    musicId: "3",
    index: 0,
    version: 6,
  });
});

socketbis.on("join_playlist", (data) => {
  console.log("✅ join_playlist socketbis:", data);
});

socketbis.on("music_added", (data) => {
  console.log("✅ music_added socketbis:", data);
});

socketbis.on("music_moved", (data) => {
  console.log("✅ music_moved socketbis:", data);
});

socketbis.on("playlist_updated", (data) => {
  console.log("✅ playlist_updated socketbis:", data);
});
