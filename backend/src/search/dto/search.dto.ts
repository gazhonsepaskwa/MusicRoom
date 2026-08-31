import { ApiProperty } from '@nestjs/swagger';
import { IsInt, IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class SearchQueryDto {
  @ApiProperty({ example: 'queen', description: 'Search text to look up.' })
  @IsString()
  @IsNotEmpty()
  query!: string;

  @ApiProperty({ example: 'artist,music', required: false, description: 'Comma-separated entity types to search.' })
  @IsOptional()
  @IsString()
  type?: string;

  @ApiProperty({ example: 0, required: false })
  @IsOptional()
  @IsInt()
  offset?: number;

  @ApiProperty({ example: 10, required: false })
  @IsOptional()
  @IsInt()
  limit?: number;
}
