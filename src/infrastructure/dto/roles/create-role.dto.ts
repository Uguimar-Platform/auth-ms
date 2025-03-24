import {
  IsArray,
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsString,
} from 'class-validator';
export class CreateRoleDto {
  @IsOptional()
  id: number;

  @IsString()
  @IsNotEmpty()
  name: string;

  @IsNotEmpty()
  @IsNumber()
  status: number;

  @IsNotEmpty()
  @IsArray()
  @IsNumber({}, { each: true })
  permissionsId: number[];

  @IsOptional()
  createdAt: Date;

  @IsOptional()
  updatedAt?: Date;
}
