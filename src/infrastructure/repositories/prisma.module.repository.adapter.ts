import { HttpStatus, Injectable } from '@nestjs/common';
import { RpcException } from '@nestjs/microservices';
import { PrismaClient } from '@prisma/client';
import { ModuleModel } from 'src/domain/models/module.model';
import { ModuleRepositoryPort } from 'src/domain/repositories/module.repository.port';
import { PaginatedFilterDto } from '../dto/list-module.filter.dto';

/**
 * Adapter to the module repository using Prisma.
 */

@Injectable()
export class PrismaModuleRepositoryAdapter implements ModuleRepositoryPort {
  /**
   * Constructor of the class.
   *
   * @param prisma The Prisma client.
   */
  constructor(private prisma: PrismaClient) {}

  /**
   * Method that creates a module.
   *
   * @param module The module to be created.
   * @returns The created module.
   */

  async createModule(module: ModuleModel) {
    const createdModule = await this.prisma.module.create({
      data: module,
    });

    return createdModule;
  }

  /**
   *
   * @param id The id of the module to be found.
   * @returns The module found.
   */

  async findModuleById(id: number) {
    const module = await this.prisma.module.findUnique({
      where: {
        id,
      },
    });

    if (!module) {
      throw new Error('Module not found');
    }

    return module;
  }

  async updateModule(id: number, module: ModuleModel) {
    const updatedModule = await this.prisma.module.update({
      where: { id },
      data: {
        ...module,
        updatedAt: new Date(),
      },
    });

    return updatedModule;
  }

  async updateModuleStatus(id: number, status: number) {
    try {
      const module = await this.prisma.module.findUnique({
        where: { id },
      });

      if (!module) {
        throw new RpcException({
          statusCode: HttpStatus.NOT_FOUND,
          message: 'Module not found',
        });
      }

      const updatemoduleStatus = await this.prisma.module.update({
        where: { id },
        data: {
          status: status,
          updatedAt: new Date(),
        },
      });

      return updatemoduleStatus;
    } catch (error) {
      throw new RpcException({
        statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
        message: 'An error occurred while updating the module status',
      });
    }
  }

  async listModulePaginated(filter: PaginatedFilterDto) {
    const { page, limit } = filter;

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
