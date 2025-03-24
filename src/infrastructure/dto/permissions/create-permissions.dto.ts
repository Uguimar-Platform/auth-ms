import {
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsString,
} from 'class-validator';
export class CreatePermissionDto {
  @IsOptional()
  id: number;

  @IsString()
  @IsNotEmpty()
  name: string;

  @IsString()
  @IsNotEmpty()
  slug: string;

  @IsNotEmpty()
  @IsNumber()
  moduleId: number;

  @IsNotEmpty()
  @IsNumber()
  status: number;


  @IsOptional()
  updatedAt?: Date;
}
