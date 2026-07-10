-- CreateEnum
CREATE TYPE "visibilityStatus" AS ENUM ('PUBLIC', 'FRIEND', 'PRIVATE');

-- AlterTable
ALTER TABLE "music" ADD COLUMN     "lyrics" TEXT;

-- AlterTable
ALTER TABLE "user" ADD COLUMN     "address" "visibilityStatus" NOT NULL DEFAULT 'PRIVATE',
ADD COLUMN     "createdPlaylist" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
ADD COLUMN     "firstPreferredMusicId" INTEGER,
ADD COLUMN     "friends" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
ADD COLUMN     "invitedPlaylist" "visibilityStatus" NOT NULL DEFAULT 'PUBLIC',
ADD COLUMN     "secondPreferredMusicId" INTEGER,
ADD COLUMN     "thirdPreferredMusicId" INTEGER;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_firstPreferredMusicId_fkey" FOREIGN KEY ("firstPreferredMusicId") REFERENCES "music"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_secondPreferredMusicId_fkey" FOREIGN KEY ("secondPreferredMusicId") REFERENCES "music"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_thirdPreferredMusicId_fkey" FOREIGN KEY ("thirdPreferredMusicId") REFERENCES "music"("id") ON DELETE SET NULL ON UPDATE CASCADE;
