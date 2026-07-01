/*
  Warnings:

  - The primary key for the `device` table will be changed. If it partially fails, the table could be left without primary key constraint.
  - You are about to drop the column `license` on the `device` table. All the data in the column will be lost.
  - A unique constraint covering the columns `[name,ownerId]` on the table `device` will be added. If there are existing duplicate values, this will fail.

*/
-- AlterTable
ALTER TABLE "device" DROP CONSTRAINT "device_pkey",
DROP COLUMN "license",
ALTER COLUMN "id" DROP DEFAULT,
ALTER COLUMN "id" SET DATA TYPE TEXT,
ADD CONSTRAINT "device_pkey" PRIMARY KEY ("id");
DROP SEQUENCE "device_id_seq";

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

-- CreateIndex
CREATE UNIQUE INDEX "device_name_ownerId_key" ON "device"("name", "ownerId");

-- AddForeignKey
ALTER TABLE "deviceship" ADD CONSTRAINT "deviceship_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "deviceship" ADD CONSTRAINT "deviceship_deviceId_fkey" FOREIGN KEY ("deviceId") REFERENCES "device"("id") ON DELETE CASCADE ON UPDATE CASCADE;
