import { IsString, IsNumber } from 'class-validator';

export class UpdateNameDto {
  @IsString()
  id!: string;

  @IsString()
  name!: string;
}
