import { HttpStatus, Injectable } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { PrismaClient } from '@prisma/client';
import { permission } from 'process';
import { PermissionsModel } from 'src/domain/models/permissions.model';
import { PermissionsRepositoryPort } from 'src/domain/repositories/permission.repository.port';
import { PaginatedFilterDto } from '../dto/list-module.filter.dto';

@Injectable()
export class PrismaPermissionRepositoryAdapter
  implements PermissionsRepositoryPort
{
  constructor(private prisma: PrismaClient) {}

  async createPermission(permisision: PermissionsModel) {
    const createPermission = await this.prisma.permission.create({
      data: permisision,
    });

    return createPermission;
  }

  async findPermissionById(id: number) {
    const permission = await this.prisma.permission.findUnique({
      where: {
        id,
      },
    });

    if (!permission) {
      throw new Error('Permission not found');
    }

    return permission;
  }

  async updatePermission(id: number, permission: PermissionsModel) {
    const updatedPermission = await this.prisma.permission.update({
      where: { id },
      data: {
        ...permission,
        updatedAt: new Date(),
      },
    });

    return updatedPermission;
  }

  async updatePermissionStatus(id: number, status: number) {
    try {
      const permission = await this.prisma.permission.findUnique({
        where: { id },
      });

      if (!permission) {
        throw new RpcException({
          statusCode: HttpStatus.NOT_FOUND,
          message: 'Module not found',
        });
      }

      const updatePermissionStatus = await this.prisma.permission.update({
        where: { id },
        data: {
          status: status,
          updatedAt: new Date(),
        },
      });

      return updatePermissionStatus;
    } catch (error) {
      throw new RpcException({
        statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
        message: 'An error occurred while updating the module status',
      });
    }
  }

  async listPermissionPaginated(filter: PaginatedFilterDto) {
    const { page, limit } = filter;

    const totalItems = await this.prisma.permission.count();
    const currentPage = page ?? 1;
    const perPage = limit ?? 10;
    const totalPages = Math.ceil(totalItems / perPage);

    const data = await this.prisma.permission.findMany({
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
