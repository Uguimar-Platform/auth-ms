import { Injectable } from "@nestjs/common";
import { JwtService } from "@nestjs/jwt";
import { PrismaClient } from "@prisma/client";
import { envs } from "src/config";
import { UserModel } from "src/domain/models/user.model";
import { UserRepositoryPort } from "src/domain/repositories/user.repository.port";
import * as bcrypt from 'bcryptjs';
import { RpcException } from "@nestjs/microservices";
@Injectable()
export class PrismaUserRepositoryAdapter implements UserRepositoryPort {
  constructor(
    private prisma: PrismaClient,
    private jwtService: JwtService,
  ) {}

  async createUser(user: UserModel) {
  const hashedPassword = await bcrypt.hash(user.password, 10);

  const createdUser = await this.prisma.user.create({
    data: {
      ...user,
      password: hashedPassword,
    },
  });

  return {
    ...createdUser,
  } as UserModel;
}

  async loginUser(email: string, password: string) {
    const user = await this.prisma.user.findUnique({
      where: { email },
      include: { role: true },
    });

    if (!user) {
      throw new RpcException({
        status: 'BAD_REQUEST',
        message: 'Email or password not valid',
      });
    }

    const isPasswordValid = bcrypt.compareSync(password, user.password);

    if (!isPasswordValid) {
      throw new RpcException({
        status: 'BAD_REQUEST',
        message: 'Email or password not valid',
      });
    }

    const { password: _, ...rest } = user;

    const payload = { id: user.id, email: user.email };
    const accessToken = this.jwtService.sign(payload, { expiresIn: '1d' });
    const refreshToken = this.jwtService.sign(payload, { expiresIn: '7d' });

    return {
      user: rest,
      accessToken,
      refreshToken,
    };
  }
}