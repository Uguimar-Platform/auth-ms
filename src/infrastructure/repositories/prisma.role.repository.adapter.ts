import { HttpStatus, Injectable } from '@nestjs/common';
import { RoleRepositoryPort } from 'src/domain/repositories/role.repository.port';
import { RoleModel } from 'src/domain/models/role.model';
import { PrismaClient } from '@prisma/client';
import { RpcException } from '@nestjs/microservices';
import { PaginatedFilterDto } from '../dto/list-module.filter.dto';

@Injectable()
export class PrismaRoleRepositoryAdapter implements RoleRepositoryPort {
  constructor(private prisma: PrismaClient) {}

  moduleText: string = 'role';

  async createRole(role: RoleModel) {
    try {
      const existingrole = await this.prisma.role.findFirst({
        where: { name: role.name },
      });

      if (existingrole) {
        throw new RpcException({
          statusCode: HttpStatus.BAD_REQUEST,
          message: `The role with this name already exists.`,
        });
      }

      const permissions = await this.prisma.permission.findMany({
        where: {
          id: {
            in: role.permissionsId,
          },
        },
      });

      if (permissions.length !== role.permissionsId.length) {
        throw new RpcException({
          statusCode: HttpStatus.BAD_REQUEST,
          message: 'One or more permissions do not exist.',
        });
      }

      await this.prisma.role.create({
        data: {
          name: role.name,
          rolePermissions: {
            create: role.permissionsId.map((permissionId) => ({
              permissionId,
            })),
          },
        },
      });

      return {
        statusCode: HttpStatus.OK,
        message: `${this.moduleText.charAt(0).toUpperCase() + this.moduleText.slice(1)} created successfully.`,
      };
    } catch (error) {
      if (error instanceof RpcException) {
        throw error; // No lo envuelvas de nuevo
      }

      throw new RpcException({
        statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
        message: `There was a problem creating the ${this.moduleText}. Please try again.`,
        error: error.message || 'Unexpected error',
      });
    }
  }

  async findRoleById(id: number) {
    const role = await this.prisma.role.findUnique({
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

  async listRolePaginated(filter: PaginatedFilterDto) {
    const { page, limit } = filter;

    const totalItems = await this.prisma.role.count();
    const currentPage = page ?? 1;
    const perPage = limit ?? 10;
    const totalPages = Math.ceil(totalItems / perPage);

    const data = await this.prisma.role.findMany({
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
