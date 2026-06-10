-- AlterTable
ALTER TABLE "user" ADD COLUMN     "firstPreferredArtistId" INTEGER,
ADD COLUMN     "secondPreferredArtistId" INTEGER,
ADD COLUMN     "thirdPreferredArtistId" INTEGER;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_firstPreferredArtistId_fkey" FOREIGN KEY ("firstPreferredArtistId") REFERENCES "artist"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_secondPreferredArtistId_fkey" FOREIGN KEY ("secondPreferredArtistId") REFERENCES "artist"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_thirdPreferredArtistId_fkey" FOREIGN KEY ("thirdPreferredArtistId") REFERENCES "artist"("id") ON DELETE SET NULL ON UPDATE CASCADE;
