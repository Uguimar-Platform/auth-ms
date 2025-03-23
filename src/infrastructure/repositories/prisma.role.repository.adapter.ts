import { HttpStatus, Injectable } from '@nestjs/common';
import { RoleRepositoryPort } from 'src/domain/repositories/role.repository.port';
import { RoleModel } from 'src/domain/models/role.model';
import { PrismaClient } from '@prisma/client';
import { RpcException } from '@nestjs/microservices';

@Injectable()
export class PrismaRoleRepositoryAdapter implements RoleRepositoryPort {
  constructor(private prisma: PrismaClient) {}

  async createRole(role: RoleModel) {
    const createRole = await this.prisma.role.create({
      data: role,
    });

    return {
      ...createRole,
    } as RoleModel;
  }

  async findRoleById(id: number) {
    const role = await this.prisma.module.findUnique({
      where: {
        id,
      },
    });

    if (!role) {
      throw new Error('Role not found');
    }

    return role;
  }

  async updateRole(id: number, role: RoleModel) {
    const updatedRole = await this.prisma.role.update({
      where: { id },
      data: {
        ...role,
        updatedAt: new Date(),
      },
    });

    return updatedRole;
  }

  async updateRoleStatus(id: number, status: number) {
    try {
      const role = await this.prisma.role.findUnique({
        where: { id },
      });

      if (!role) {
        throw new RpcException({
          statusCode: HttpStatus.NOT_FOUND,
          message: 'Role not found',
        });
      }

      const updateRoleStatus = await this.prisma.role.update({
        where: { id },
        data: {
          status: status,
          updatedAt: new Date(),
        },
      });

      return updateRoleStatus;
    } catch (error) {
      throw new RpcException({
        statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
        message: 'An error occurred while updating the module status',
      });
    }
  }

  async listRolePaginated(module: RoleModel) {
    const { page, limit } = module;

    const totalItems = await this.prisma.module.count();
    const currentPage = page ?? 1;
    const perPage = limit ?? 10;
    const totalPages = Math.ceil(totalItems / perPage);

    const data = await this.prisma.module.findMany({
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
