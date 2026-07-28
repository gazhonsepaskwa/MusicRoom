/*
  Warnings:

  - Made the column `email` on table `user` required. This step will fail if there are existing NULL values in that column.

*/
-- AlterTable
ALTER TABLE "user" ALTER COLUMN "password" DROP NOT NULL,
ALTER COLUMN "email" SET NOT NULL;
