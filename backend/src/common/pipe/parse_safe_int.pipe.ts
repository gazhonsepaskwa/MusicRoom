import { PipeTransform, Injectable, BadRequestException } from '@nestjs/common';

@Injectable()
export class ParseSafeIntPipe implements PipeTransform<string, number> {
  transform(value: string): number {
    if (value === undefined || value === null) {
      throw new BadRequestException('ID is required');
    }
    if (value.length > 15) {
      throw new BadRequestException('ID is too large');
    }
    const num = Number(value);
    if (isNaN(num) || !Number.isInteger(num)) {
      throw new BadRequestException('ID must be an integer');
    }
    return num;
  }
}
