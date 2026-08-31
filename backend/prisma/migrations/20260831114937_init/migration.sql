-- CreateExtension
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- CreateEnum
CREATE TYPE "visibilityStatus" AS ENUM ('PUBLIC', 'FRIEND', 'PRIVATE');

-- CreateEnum
CREATE TYPE "invitationStatus" AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED', 'NOTVIEWED');

-- CreateEnum
CREATE TYPE "licenseStatus" AS ENUM ('NONE', 'TRIAL', 'EVERYTHING');

-- CreateTable
CREATE TABLE "user" (
    "id" SERIAL NOT NULL,
    "password" TEXT,
    "username" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "verifiedEmail" BOOLEAN NOT NULL DEFAULT false,
    "firstPreferredMusicId" INTEGER,
    "secondPreferredMusicId" INTEGER,
    "thirdPreferredMusicId" INTEGER,
    "showAddress" "visibilityStatus" NOT NULL DEFAULT 'PRIVATE',
    "showFriends" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
    "showPreferedMusics" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
    "showCreatedPlaylist" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
    "showInvitedPlaylist" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
    "tempPin" TEXT,

    CONSTRAINT "user_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "friendship" (
    "requesterId" INTEGER NOT NULL,
    "addresseeId" INTEGER NOT NULL,
    "status" "invitationStatus" NOT NULL DEFAULT 'NOTVIEWED',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "friendship_pkey" PRIMARY KEY ("requesterId","addresseeId")
);

-- CreateTable
CREATE TABLE "playlistship" (
    "addresseeId" INTEGER NOT NULL,
    "playlistId" INTEGER NOT NULL,
    "status" "invitationStatus" NOT NULL DEFAULT 'NOTVIEWED',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "playlistship_pkey" PRIMARY KEY ("playlistId","addresseeId")
);

-- CreateTable
CREATE TABLE "device" (
    "id" TEXT NOT NULL,
    "ownerId" INTEGER NOT NULL,
    "name" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "device_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "deviceship" (
    "deviceId" TEXT NOT NULL,
    "userId" INTEGER NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "canSeek" BOOLEAN NOT NULL DEFAULT false,
    "canTogglePlayPause" BOOLEAN NOT NULL DEFAULT false,
    "canModifyMusic" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "deviceship_pkey" PRIMARY KEY ("deviceId","userId")
);

-- CreateTable
CREATE TABLE "playlist" (
    "id" SERIAL NOT NULL,
    "isPublic" BOOLEAN NOT NULL,
    "title" TEXT NOT NULL,
    "userId" INTEGER NOT NULL,
    "isDefault" BOOLEAN NOT NULL DEFAULT false,
    "version" INTEGER NOT NULL DEFAULT 0,
    "status" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "playlist_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "playlistMusic" (
    "playlistId" INTEGER NOT NULL,
    "musicId" INTEGER NOT NULL,
    "index" INTEGER NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "playlistMusic_pkey" PRIMARY KEY ("playlistId","musicId")
);

-- CreateTable
CREATE TABLE "music" (
    "id" SERIAL NOT NULL,
    "spotifyId" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "duration" INTEGER NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "albumIndex" INTEGER NOT NULL,
    "lyrics" TEXT,
    "albumId" INTEGER NOT NULL,

    CONSTRAINT "music_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "album" (
    "id" SERIAL NOT NULL,
    "spotifyId" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "date" TIMESTAMP(3) NOT NULL,
    "images" TEXT[],
    "coverUrl" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "album_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "artist" (
    "id" SERIAL NOT NULL,
    "spotifyId" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "images" TEXT[],
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "artist_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "_albumToartist" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,

    CONSTRAINT "_albumToartist_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateTable
CREATE TABLE "_artistTomusic" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,

    CONSTRAINT "_artistTomusic_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateIndex
CREATE UNIQUE INDEX "user_username_key" ON "user"("username");

-- CreateIndex
CREATE UNIQUE INDEX "user_email_key" ON "user"("email");

-- CreateIndex
CREATE INDEX "friendship_addresseeId_idx" ON "friendship"("addresseeId");

-- CreateIndex
CREATE UNIQUE INDEX "device_name_ownerId_id_key" ON "device"("name", "ownerId", "id");

-- CreateIndex
CREATE INDEX "playlist_title_idx" ON "playlist"("title");

-- CreateIndex
CREATE UNIQUE INDEX "playlist_title_userId_isDefault_key" ON "playlist"("title", "userId", "isDefault");

-- CreateIndex
CREATE UNIQUE INDEX "music_spotifyId_key" ON "music"("spotifyId");

-- CreateIndex
CREATE INDEX "Music_title_trgm_idx" ON "music" USING GIN ("title" gin_trgm_ops);

-- CreateIndex
CREATE UNIQUE INDEX "album_spotifyId_key" ON "album"("spotifyId");

-- CreateIndex
CREATE INDEX "Album_title_trgm_idx" ON "album" USING GIN ("title" gin_trgm_ops);

-- CreateIndex
CREATE UNIQUE INDEX "artist_spotifyId_key" ON "artist"("spotifyId");

-- CreateIndex
CREATE INDEX "Artist_title_trgm_idx" ON "artist" USING GIN ("title" gin_trgm_ops);

-- CreateIndex
CREATE INDEX "_albumToartist_B_index" ON "_albumToartist"("B");

-- CreateIndex
CREATE INDEX "_artistTomusic_B_index" ON "_artistTomusic"("B");

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_firstPreferredMusicId_fkey" FOREIGN KEY ("firstPreferredMusicId") REFERENCES "music"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_secondPreferredMusicId_fkey" FOREIGN KEY ("secondPreferredMusicId") REFERENCES "music"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_thirdPreferredMusicId_fkey" FOREIGN KEY ("thirdPreferredMusicId") REFERENCES "music"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "friendship" ADD CONSTRAINT "friendship_requesterId_fkey" FOREIGN KEY ("requesterId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "friendship" ADD CONSTRAINT "friendship_addresseeId_fkey" FOREIGN KEY ("addresseeId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "playlistship" ADD CONSTRAINT "playlistship_addresseeId_fkey" FOREIGN KEY ("addresseeId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "playlistship" ADD CONSTRAINT "playlistship_playlistId_fkey" FOREIGN KEY ("playlistId") REFERENCES "playlist"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "device" ADD CONSTRAINT "device_ownerId_fkey" FOREIGN KEY ("ownerId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "deviceship" ADD CONSTRAINT "deviceship_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "deviceship" ADD CONSTRAINT "deviceship_deviceId_fkey" FOREIGN KEY ("deviceId") REFERENCES "device"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "playlist" ADD CONSTRAINT "playlist_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "playlistMusic" ADD CONSTRAINT "playlistMusic_playlistId_fkey" FOREIGN KEY ("playlistId") REFERENCES "playlist"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "playlistMusic" ADD CONSTRAINT "playlistMusic_musicId_fkey" FOREIGN KEY ("musicId") REFERENCES "music"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "music" ADD CONSTRAINT "music_albumId_fkey" FOREIGN KEY ("albumId") REFERENCES "album"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_albumToartist" ADD CONSTRAINT "_albumToartist_A_fkey" FOREIGN KEY ("A") REFERENCES "album"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_albumToartist" ADD CONSTRAINT "_albumToartist_B_fkey" FOREIGN KEY ("B") REFERENCES "artist"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_artistTomusic" ADD CONSTRAINT "_artistTomusic_A_fkey" FOREIGN KEY ("A") REFERENCES "artist"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_artistTomusic" ADD CONSTRAINT "_artistTomusic_B_fkey" FOREIGN KEY ("B") REFERENCES "music"("id") ON DELETE CASCADE ON UPDATE CASCADE;
