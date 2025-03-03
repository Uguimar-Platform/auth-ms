import { Injectable } from "@nestjs/common";
import { PrismaClient } from "@prisma/client";
import { createDecipheriv } from "crypto";
import { UserModel } from "src/domain/models/user.model";
import { UserRepositoryPort } from "src/domain/repositories/user.repository.port";

@Injectable()    
export class PrismaUserRepositoryAdapter implements UserRepositoryPort {
    constructor(private prisma: PrismaClient) {}
    async createUser(user:UserModel) {
        const createdUser = await this.prisma.user.create({
            data: user
        });
        return {
            id: createdUser.id,
            name: user.name,
            email: createdUser.email,
            password: createdUser.password
        } as UserModel;
    }
}