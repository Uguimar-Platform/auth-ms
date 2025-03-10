import { IsDateString, IsInt, IsNotEmpty, IsString } from 'class-validator';

export class RefreshTokenDto {
  @IsString()
  @IsNotEmpty()
  token: string;

  @IsInt()
  userId: number;

  @IsString()
  expiresAt: string;
}
