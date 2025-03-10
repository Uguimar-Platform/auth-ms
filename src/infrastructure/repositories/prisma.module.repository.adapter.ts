import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { ModuleModel } from 'src/domain/models/module.model';
import { ModuleRepositoryPort } from 'src/domain/repositories/module.repository.port';

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

  async createModule(module: ModuleModel): Promise<ModuleModel> {
    const createdModule = await this.prisma.module.create({
      data: module,
    });

    return {
      ...createdModule,
    } as ModuleModel;
  }

  /**
   * 
   * @param id The id of the module to be found. 
   * @returns The module found.
   */

  async findModuleById(id: number): Promise<ModuleModel> {
    const module = await this.prisma.module.findUnique({
      where: {
        id,
      },
    });

    if (!module) {
      throw new Error('Module not found');
    }

    return module as ModuleModel;
  }
}
