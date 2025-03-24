
import { IsNumber, IsOptional, IsString } from 'class-validator';

export class UpdateModuleDto {
  @IsNumber()
  id: number;

  @IsString()
  @IsOptional()
  name: string;

  @IsNumber()
  status: number;

  @IsOptional()
  createdAt?: Date;

  @IsOptional()
  updatedAt: Date;
}
