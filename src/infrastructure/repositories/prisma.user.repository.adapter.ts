
import { Injectable } from "@nestjs/common";
import { UserModel } from "src/domain/models/user.model";
import { UserRepositoryPort } from "src/domain/repositories/user.repository.port";
import { PrismaClient } from '@prisma/client';

@Injectable()    
export class PrismaUserRepositoryAdapter implements UserRepositoryPort {
    constructor(private prisma: PrismaClient) {}
    async createUser(user:UserModel) {
        
        const createdUser = await this.prisma.user.create({
            data: user
        });
        return {
            ...createdUser
        } as UserModel;
    }


    
}