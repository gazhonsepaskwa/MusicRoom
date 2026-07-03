import { ApiProperty } from '@nestjs/swagger';

export class SearchQueryDto {
  @ApiProperty({ example: 'queen', description: 'Search text to look up.' })
  query!: string;

  @ApiProperty({ example: 'artist,music', required: false, description: 'Comma-separated entity types to search.' })
  type?: string;

  @ApiProperty({ example: 0, required: false })
  offset?: number;

  @ApiProperty({ example: 10, required: false })
  limit?: number;
}
