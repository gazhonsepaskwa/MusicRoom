/*
  Warnings:

  - You are about to drop the column `address` on the `user` table. All the data in the column will be lost.
  - You are about to drop the column `createdPlaylist` on the `user` table. All the data in the column will be lost.
  - You are about to drop the column `friends` on the `user` table. All the data in the column will be lost.
  - You are about to drop the column `invitedPlaylist` on the `user` table. All the data in the column will be lost.

*/
-- AlterTable
ALTER TABLE "user" DROP COLUMN "address",
DROP COLUMN "createdPlaylist",
DROP COLUMN "friends",
DROP COLUMN "invitedPlaylist",
ADD COLUMN     "showAddress" "visibilityStatus" NOT NULL DEFAULT 'PRIVATE',
ADD COLUMN     "showCreatedPlaylist" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
ADD COLUMN     "showFriends" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
ADD COLUMN     "showInvitedPlaylist" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC';
