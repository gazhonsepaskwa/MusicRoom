-- AlterEnum
ALTER TYPE "invitationStatus" ADD VALUE 'NOTVIEWED';

-- AlterTable
ALTER TABLE "friendship" ALTER COLUMN "status" SET DEFAULT 'PENDING';
