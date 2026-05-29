-- CreateTable
CREATE TABLE "_artistTomusic" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,

    CONSTRAINT "_artistTomusic_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateIndex
CREATE INDEX "_artistTomusic_B_index" ON "_artistTomusic"("B");

-- AddForeignKey
ALTER TABLE "_artistTomusic" ADD CONSTRAINT "_artistTomusic_A_fkey" FOREIGN KEY ("A") REFERENCES "artist"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_artistTomusic" ADD CONSTRAINT "_artistTomusic_B_fkey" FOREIGN KEY ("B") REFERENCES "music"("id") ON DELETE CASCADE ON UPDATE CASCADE;
