import { Field, Int, ObjectType } from "@nestjs/graphql";
import { Question } from "../questions/questions.entity";
import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";

@Entity()
@ObjectType()
export class Quiz{
    
    @PrimaryGeneratedColumn()
    @Field(type => Int)
    id: number;

    @Column()
    @Field()
    name: string;

    @Column()
    @Field()
    description: string;

    @OneToMany(() => Question, question => question.quiz, {
        cascade: true
    })
    @Field(type => [Question])
    questions?: Question[];

}