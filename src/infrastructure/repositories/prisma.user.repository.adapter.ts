import { HttpStatus, Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { PrismaClient } from '@prisma/client';
import { envs } from 'src/config';
import { UserModel } from 'src/domain/models/user.model';
import { UserRepositoryPort } from 'src/domain/repositories/user.repository.port';
import * as bcrypt from 'bcryptjs';
import { RpcException } from '@nestjs/microservices';
import { PaginatedFilterDto } from '../dto/list-module.filter.dto';
import { JwtPayload } from '../interfaces/jwt-payload.interface';
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

    return createdUser;
  }

  async updateUser(id: number, user: UserModel) {
    const dataUpdate: any = { ...user };

    if (user.password) {
      const hashedPassword = await bcrypt.hash(user.password, 10);
      dataUpdate.password = hashedPassword;
    } else {
      delete dataUpdate.password;
    }

    if (user.roleId !== undefined) {
      const roleExists = await this.prisma.role.findUnique({
        where: { id: user.roleId },
      });
      if (!roleExists) {
        throw new Error(`El roleId ${user.roleId} no existe.`);
      }
    } else {
      delete dataUpdate.roleId;
    }

    const updatedUser = await this.prisma.user.update({
      where: { id },
      data: dataUpdate,
    });

    return updatedUser;
  }

  async signJWT(payload: JwtPayload) {
    return this.jwtService.sign(payload);
  }

  async signRefreshToken(payload: JwtPayload) {
    return this.jwtService.sign(payload, {
      secret: envs.jwtSecret,
      expiresIn: '7d',
    });
  }

  async loginUser(email: string, password: string) {
    const user = await this.prisma.user.findUnique({
      where: { email },
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

    const accessToken = await this.signJWT({ ...rest });
    const refreshToken = await this.signRefreshToken({ ...rest });

    return {
      user,
      accessToken,
      refreshToken,
    };
  }

  async updateUserStatus(id: number, status: number) {
    try {
      const user = await this.prisma.user.findUnique({
        where: { id },
      });

      if (!user) {
        throw new RpcException({
          statusCode: HttpStatus.NOT_FOUND,
          message: 'User not found',
        });
      }

      const updateUserStatus = await this.prisma.user.update({
        where: { id },
        data: {
          status: status,
          updatedAt: new Date(),
        },
      });

      return updateUserStatus;
    } catch (error) {
      throw new RpcException({
        statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
        message: 'An error occurred while updating the module status',
      });
    }
  }

  async listUserPaginated(filter: PaginatedFilterDto) {
    const { page, limit } = filter;

    const totalItems = await this.prisma.user.count();
    const currentPage = page ?? 1;
    const perPage = limit ?? 10;
    const totalPages = Math.ceil(totalItems / perPage);

    const data = await this.prisma.user.findMany({
      skip: (currentPage - 1) * perPage,
      take: perPage,
      orderBy: { createdAt: 'desc' },
    });

    return {
      data,
      totalPages,
      totalItems,
      page: currentPage,
      limit: perPage,
    };
  }
}
