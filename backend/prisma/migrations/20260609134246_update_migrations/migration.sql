/*
  Warnings:

  - A unique constraint covering the columns `[title,userId,isDefault]` on the table `playlist` will be added. If there are existing duplicate values, this will fail.
  - A unique constraint covering the columns `[username]` on the table `user` will be added. If there are existing duplicate values, this will fail.
  - A unique constraint covering the columns `[email]` on the table `user` will be added. If there are existing duplicate values, this will fail.
  - Made the column `email` on table `user` required. This step will fail if there are existing NULL values in that column.

*/
-- DropIndex
DROP INDEX "album_title_idx";

-- DropIndex
DROP INDEX "artist_title_idx";

-- DropIndex
DROP INDEX "music_title_idx";

-- AlterTable
ALTER TABLE "playlist" ADD COLUMN     "isDefault" BOOLEAN NOT NULL DEFAULT false;

-- AlterTable
ALTER TABLE "playlistship" ALTER COLUMN "status" SET DEFAULT 'PENDING';

-- AlterTable
ALTER TABLE "user" ADD COLUMN     "verifiedEmail" BOOLEAN NOT NULL DEFAULT false,
ALTER COLUMN "password" DROP NOT NULL,
ALTER COLUMN "email" SET NOT NULL;

-- CreateIndex
CREATE INDEX "Album_title_trgm_idx" ON "album" USING GIN ("title" gin_trgm_ops);

-- CreateIndex
CREATE INDEX "Artist_title_trgm_idx" ON "artist" USING GIN ("title" gin_trgm_ops);

-- CreateIndex
CREATE INDEX "Music_title_trgm_idx" ON "music" USING GIN ("title" gin_trgm_ops);

-- CreateIndex
CREATE UNIQUE INDEX "playlist_title_userId_isDefault_key" ON "playlist"("title", "userId", "isDefault");

-- CreateIndex
CREATE UNIQUE INDEX "user_username_key" ON "user"("username");

-- CreateIndex
CREATE UNIQUE INDEX "user_email_key" ON "user"("email");
